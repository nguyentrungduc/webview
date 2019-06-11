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

### Binding JavaScript code to Android code
- Khi phát triển web appp với 1 design cụ thể cgi webciew in android app,ta cần tạo interface với đoạn JavaScript code và client-side với Android code. Ví dụ, JavaScript code vần gọi method của Android code là show Dialog, với JavaScript là sử dụng alert() function
- Để bind 1 interface JavaScript với Android code, sử dụng addJavascriptInterface(), truyền cho nó 1 class instance để bind JavaScipt và 1 interface

          class WebAppInterface(private val mContext: Context) {

              /** Show a toast from the web page  */
              @JavascriptInterface
              fun showToast(toast: String) {
                  Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show()
              }
          }
          
- Ví dụ trên, WebAppInterface class cung cấp 1 webpage để tạo 1 toast, sử dụng show Toast mehtods
- Ta có thể bind class JavaScript chạy trên Webview với addJavascriptInterface() với tên interface là Android

          val webView: WebView = findViewById(R.id.webview)
          webView.addJavascriptInterface(WebAppInterface(this), "Android")
          
- Nó sẽ tạo 1 interface gọi Android cho JavaScript chạy trên WebView. Tại điểm này, web app có truy cập vào WebAppInterface. Ví dụ, đây laf đoạn HTML và JS tạo message show Toast sử dụng interface khi click vào button 

            <input type="button" value="Say hello" onClick="showAndroidToast('Hello Android!')" />

                    <script type="text/javascript">
                    function showAndroidToast(toast) {
                    Android.showToast(toast);
                    }
                    </script>
                    
- Ko cần phải khởi tạo interface từ JavaScript. WebView tự động làm sẵn cho web app page. Vậy, khi click vào button, showAndroidToast() function sử dụng Android interface để gọi WebAppInteface.showToast()

- Chú ý : Khi sử dụng  addJavascriptInterface() nghĩa là bạn đã đồng ý cho JS control app của bạn. Nó thực sự tốt để làm các feature nhưng nó cũng có thể xảy ra các issue. Khi HTML trong webview không đáng tin cậy (ví dụ, một phần trong HTML ccung cấp bởi unknown person or process), sau đó tấn công có thể gồm HTML rằng chạy trong client side code và bất kỳ đoạn mã nào mà kể tấn công chọn. Vì vậy bạn ko nên sử dụng addJsInterface() trừ khi bạn viết hòan toàn đoạn code đó. Bạn cũng ko nên đồng ý user navigate tới bất kì web khác bạn ko sở hữu nó

### Handling page navigation
- Khi user click vào 1 link từ webview, theo mặc định xử lý của Android sẽ launch app và handles URLs. Theo defailt web browser mở và load đích URL. Tuy nhiên, bạn có thể override nó bởi webview, để link tới webview của bạn. Bạn có thể cho ng dùng navigate backwark và thông qua web page history để duy trì web của bạn
- Để open links clicked bởi user, cung cấp 1 WebViewClient cho webview, sử dụng setWebViewClient() 
          
          val myWebView: WebView = findViewById(R.id.webview)
          webView.webViewClient = WebViewClient()

- Nếu bạ muốn kiểm soát nhiều hơn khi click vào link, tạo WebViewClient đc override method shouldOverrideUrlLoading() :

          private class MyWebViewClient : WebViewClient() {

              override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                  if (Uri.parse(url).host == "www.example.com") {
                      // This is my web site, so do not override; let my WebView load the page
                      return false
                  }
                  // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
                  Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                      startActivity(this)
                  }
                  return true
              }
          }

          val myWebView: WebView = findViewById(R.id.webview)
          myWebView.webViewClient = MyWebViewClient()
          
- Giờ khi user click vào link, hệ thống sẽ gọi sholdOverrideUrlLoading(), nó sẽ kiểm tra xem Url host có match với "www.example.com" hay không. Nếu match , method sẽ return false và ko override method Url loading -> nó sẽ cho phép webview load url bình thường. Còn ko , sẽ gọi intent view by url 

### Navigating web page history
- Khi webview override Url loading, nó sẽ tự động lưu lại web history các page đã vào. ta có thể backward và forward với method goBack() và goForward()

                    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
                        // Check if the key event was the Back button and if there's history
                        if (keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()) {
                            myWebView.goBack()
                            return true
                        }
                        // If it wasn't the Back key or there's no web page history, bubble up to the default
                        // system behavior (probably exit the activity)
                        return super.onKeyDown(keyCode, event)
                    }

- Method canGoBack() return true nếu nó thực sự có web trc nó. Tương tự như vậy ta có thể call canGoForward() khi có forward history. Nếu ko check khi người dùng đến end history , goBack() và goForward() sẽ ko làm gì cả.

## Managing WebView object
- Android cung cấp một số API để giúp bạn quản lý các WebView object hiển thị nội dung web trong ứng dụng của bạn. 

### Version API
- Từ Android 7.0, ta có thể chọn các package khác nhau để hiện thi webview. Với AndroidX webkit library bao gồm getCurrentWebViewPackage() method để fetching infomation cần thiết để hiển thị nội dung web trong ứng dụng của bạn. Method này thực sự hữu ích khi bạn phân tích error xảy ra khi app của bạn cố gắng hiển thị nội dung web sử dụng paricular package's cài đặt của webview.

          val webViewPackageInfo = WebViewCompat.getCurrentWebViewPackage(appContext)
          Log.d("MY_APP_TAG", "WebView version: ${webViewPackageInfo.versionName}")

### Google Safe Browsing Service
- Để cung cấp cho người dùng của bạn 1 trải nghiệm an toàn, wbeview của bạn có thể verify bằng  Google Safe Browsing, nó sẽ enable app của bạn để show cho user cảnh bảo khi mà họ chuyển đến một website ko an toàn
- Giá trị mặc định của EnableSafeBrowsing là true, thỉnh thoảng có những case mà có thể bạn muốn chỉ bật Safe Browsing  hay tắt nó. Với Android 8.0 sử dụng setSafeBrowsingEnabled()
- Nếu bạn muốn tất cả các webview object từ chối check safe browing, bạn có thể add vào thẻ meta data : 

          <manifest>
              <application>
                  <meta-data android:name="android.webkit.WebView.EnableSafeBrowsing"
                             android:value="false" />
                  ...
              </application>
          </manifest>
          
### Defining programmatic actions
- Khi webview cố tải một trang được Google phân loại là mối nguy hiểm, webview theo mặc định hiển thị một quảng cáo xen kẽ vào cảnh báo người dùng về mối đe dọa đó. Màn hình cung cấp cho người dùng tùy chọn tải URL bằng mọi cách hoặc quay lại trang trước an toàn 

- Nếu từ Android 8.1 trở lên, ta có thể define theo cách lập trình các ứng dujng của bạn đối phó với mối đe dọa đã biết
- Có thể điều khiển khi nào app sẽ reports mối nguy hại đến Safe Browing
- Bạn có thể để ứng dụng của mình tự động thực hiện các hành động cụ thể, chẳng hạn như quay trơ lại an toàn, mỗi khi có warning

          private lateinit var superSafeWebView: WebView
          private var safeBrowsingIsInitialized: Boolean = false

          // ...

          override fun onCreate(savedInstanceState: Bundle?) {
              super.onCreate(savedInstanceState)

              superSafeWebView = WebView(this)
              superSafeWebView.webViewClient = MyWebViewClient()
              safeBrowsingIsInitialized = false

              if (WebViewFeature.isFeatureSupported(WebViewFeature.START_SAFE_BROWSING)) {
                  WebViewCompat.startSafeBrowsing(this, ValueCallback<Boolean> { success ->
                      safeBrowsingIsInitialized = true
                      if (!success) {
                          Log.e("MY_APP_TAG", "Unable to initialize Safe Browsing!")
                      }
                  })
              }
          }
          
          class MyWebViewClient : WebViewClientCompat() {
              // Automatically go "back to safety" when attempting to load a website that
              // Google has identified as a known threat. An instance of WebView calls
              // this method only after Safe Browsing is initialized, so there's no
              // conditional logic needed here.
              override fun onSafeBrowsingHit(
                      view: WebView,
                      request: WebResourceRequest,
                      threatType: Int,
                      callback: SafeBrowsingResponseCompat
              ) {
                  // The "true" argument indicates that your app reports incidents like
                  // this one to Safe Browsing.
                  if (WebViewFeature.isFeatureSupported(WebViewFeature.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY)) {
                      callback.backToSafety(true)
                      Toast.makeText(view.context, "Unsafe web page blocked.", Toast.LENGTH_LONG).show()
                  }
              }
          }
  
### HTML 5 Geolocation API
- Với api 23 trở lên, Geolocation chỉ support cho secure origins, như HTTPS. Một số request với GEOLocation Api từ no-secure sẽ auto bị từ chối mà ko cần gọi method onGeolocationPermissionsShowPrompt(). 

### Opting out of metrics collection
- Webview có khả năng load dữ liệu ẩn danh lên Google khi người dùng đã đồng ý. Dữ liệu được thu thập trên cơ sở mỗi ứng dụng cho mỗi ứng dụng khởi tạo Webview. Bạn có thể bỏ feature này bằng 

          <manifest>
              <application>
              ...
              <meta-data android:name="android.webkit.WebView.MetricsOptOut"
                         android:value="true" />
              </application>
          </manifest>

  

