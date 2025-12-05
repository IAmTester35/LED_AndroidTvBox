1. Khoá chết: adb shell cmd package set-home-activity com.reecotech.androidtvbox/.MainActivity
2. Mở chết: adb shell cmd package set-home-activity com.android.settings/.Setting+s
3. Password để thoát app là: vinhlong1@
4. Gọi đến Firebase Remote Config ( cache 3 tiếng ) để lấy thông tin về có bản update mới hay không, 2 trường sử dụng là latest_version_code và apk_download_url, nếu có bản update mới thì tự cập nhật (silent update)
