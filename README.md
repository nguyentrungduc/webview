# webview
## Introduce
- Nếu bạn muốn tạo một web application hay chỉ là một web page là một phần của ứng dụng, ta có thể sử dụng Webview. Webview là một class extends của Android View và nó cho phép ta hiển thị web pages như 1 phần của activity layout. Nó không bao gồm các feature của một trình duyệt web được phát triển đầy đủ, như navigation control hay address bar. Tất cả những thứ webview làm là show webpage
- Một kịch bản thường thấy khi sử dụng webview là nó  giúp ích khi bạn muốn cung cấp thông tin cho app của bạn rằng nó cần được update, ví dụ như 1 end-user ok với user guide. Với android app, bạn có thể tạo 1 activity chưa webview, sau đó user hiển thị doucment được lưu trữ trực tuyến
- Một kịch bản khác. Webview có thể giúp app của bạn luôn cần kết nối với Internet để nhận về dữ liệu chẳng hạn như email. Trong case này, bạn có thể thấy rằng việc xây dựng một webview trong ứng dụng Android của mình hiển thị một trang web với tất cả dữ liệu cho người dùng một cách dễ dàng hơn thay vì thực hiển các network request, sau đó parse lấy data rồi hiển thị lên layout. Thay vào đó bạn có thể thiết kế một trang web phù hợp với thiết bị Android và sau đó triển khai Webview trong ứng dụng của bạn 
## Basic
### Adding webview vào app của bạn
- Add Webview to layout 

          <WebView
                android:id="@+id/webview"
                android:layout_width="match_parent"
                android:layout_height="match_parent"
            />
            
- Load url 
  
              val myWebView: WebView = findViewById(R.id.webview)
              myWebView.loadUrl("http://www.example.com")
              
- Add webview trong onCreate() activity:
  
            val myWebView = WebView(activityContext)
            setContentView(myWebView)
  - Load pages
        
          myWebView.loadUrl("http://www.example.com")

- Load url từ một HTML string:

          // Create an unencoded HTML string
          // then convert the unencoded HTML string into bytes, encode
          // it with Base64, and load the data.
          val unencodedHtml =
                  "&lt;html&gt;&lt;body&gt;'%23' is the percent code for ‘#‘ &lt;/body&gt;&lt;/html&gt;"
          val encodedHtml = Base64.encodeToString(unencodedHtml.toByteArray(), Base64.NO_PADDING)
          myWebView.loadData(encodedHtml, "text/html", "base64")
          
  - Để webview hoạt động, cần add Internet permission
  
              <manifest ... >
                  <uses-permission android:name="android.permission.INTERNET" />
                  ...
              </manifest>
              
- Ngoài ra ta có thể để chế độ support fullscreen với WebChormeClient. Class này được gọi khi mà webview cần permission thay đổi host app's UI, như là tạo hay đóng windows và gửi JavaScript dialogs cho người dùng
- Xử lý events khi ảnh hướng vào nội dung rendeing , như là lỗi tuef form submit hay navigation với WebViewClient. Bạn cần sử dụng subclass để chặn URL loading
- Để enable JavaScript sử dụng WebSetting
## Using JavaScrpit trong WebView
- Nếu webpage của bạn cần load webview sử dụng JavaScript, ta cần enable JavaScript cho webview. Khi JavaScript được bật, bạn cần phải tạo interface giữa app và javascript code
### Enable JavaScript
- JavaScript disable với WebView default. Bạn có thể bất nó với WebSettings đính kèm với Webview của bạn. Bạn có thể retrievw WebSettings với methos getSetting(), sau đó enable JavaScrpit với setJavaScriptEnabled()

                    val myWebView: WebView = findViewById(R.id.webview)
                    myWebView.settings.javaScriptEnabled = true
                    
- WebSettings cung cấp truy cập vào một loạt các cài đặt khác khá hữu ích. Ví dụ, nếu bạn cần phát triển tính năng web và được designed cụ thể với Webview trên ứng dụng của bạn, bạn cần định nghĩa tùy độ tuổi người dùng với setUserAgentString(), sau đó query các end-user trong trang web để xác minh rằng ứng dụng khách ưu cầu trang web của bạn thực sự là ứng dụng Android

- Binding JavaScript code to Android code





  
  


          
  
  

