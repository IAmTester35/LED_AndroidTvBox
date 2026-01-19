function cleanOldLogsNative() {
    // Hidden service account secret token, access GAS to get this secret token
    const serviceAccount = {};

    // 1. Mốc thời gian hiện tại (ISO Format)
    // Các log có expireAt <= Now sẽ bị xóa
    const nowISO = new Date().toISOString();

    Logger.log("Bắt đầu quét log hết hạn tại thời điểm: " + nowISO);

    try {
        const token = getFirestoreAuthToken(serviceAccount.client_email, serviceAccount.private_key);
        const baseUrl = `https://firestore.googleapis.com/v1/projects/${serviceAccount.project_id}/databases/(default)/documents`;

        // 2. Query tìm log có expireAt <= hiện tại
        const queryPayload = {
            "structuredQuery": {
                "from": [{ "collectionId": "api_logs", "allDescendants": true }],
                "where": {
                    "fieldFilter": {
                        "field": { "fieldPath": "expireAt" },
                        "op": "LESS_THAN_OR_EQUAL",
                        "value": { "timestampValue": nowISO }
                    }
                }
            }
        };

        const response = UrlFetchApp.fetch(`${baseUrl}:runQuery`, {
            method: "post",
            contentType: "application/json",
            headers: { "Authorization": "Bearer " + token },
            payload: JSON.stringify(queryPayload)
        });

        const results = JSON.parse(response.getContentText());

        // Firestore runQuery trả về mảng document
        const validDocs = results.filter(item => item.document);

        if (validDocs.length === 0) {
            Logger.log("Không có log nào hết hạn.");
            return;
        }

        Logger.log(`Tìm thấy ${validDocs.length} log hết hạn. Bắt đầu xóa theo batch...`);

        // 3. Thực hiện xóa theo Batch (Tối đa 500 writes mỗi request theo giới hạn Firestore)
        const batchSize = 500;
        for (let i = 0; i < validDocs.length; i += batchSize) {
            const chunk = validDocs.slice(i, i + batchSize);
            const writes = chunk.map(item => ({
                "delete": item.document.name
            }));

            const batchPayload = { "writes": writes };

            UrlFetchApp.fetch(`${baseUrl}:commit`, {
                method: "post",
                contentType: "application/json",
                headers: { "Authorization": "Bearer " + token },
                payload: JSON.stringify(batchPayload)
            });

            Logger.log(`Đã xóa thành công ${i + chunk.length} logs...`);
        }

        Logger.log(`Hoàn tất dọn dẹp!`);

    } catch (e) {
        Logger.log("LỖI: " + e.toString());
    }
}

// Hàm bổ trợ để lấy Token từ Service Account (Tự viết, không dùng thư viện)
function getFirestoreAuthToken(email, key) {
    const header = JSON.stringify({ "alg": "RS256", "typ": "JWT" });
    const now = Math.floor(Date.now() / 1000);
    const claim = JSON.stringify({
        "iss": email,
        "scope": "https://www.googleapis.com/auth/datastore",
        "aud": "https://oauth2.googleapis.com/token",
        "exp": now + 3600,
        "iat": now
    });

    const encodedHeader = Utilities.base64EncodeWebSafe(header);
    const encodedClaim = Utilities.base64EncodeWebSafe(claim);
    const signature = Utilities.computeRsaSha256Signature(encodedHeader + "." + encodedClaim, key);
    const encodedSignature = Utilities.base64EncodeWebSafe(signature);
    const jwt = encodedHeader + "." + encodedClaim + "." + encodedSignature;

    const response = UrlFetchApp.fetch("https://oauth2.googleapis.com/token", {
        method: "post",
        payload: {
            grant_type: "urn:ietf:params:oauth:grant-type:jwt-bearer",
            assertion: jwt
        }
    });
    return JSON.parse(response.getContentText()).access_token;
}