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

### Termination Handling API
- API này xử lý các trường hợp trong đó quá trình render cho một WebView biến mất, vì hệ thống đã kill việc render để lấy lại bộ nhớ rất cần thiết hoặc do chính quá trình render bị lỗi. Bằng cách sử dụng API này, bạn cho phép ứng dụng của mình tiếp tục thực thi, mặc dù quá trình kết xuất đã biến mất.
  
- Neus quá trình render bị lỗi khi đang load page, cố gắng tải lại thì kết qủa vẫn tương tự 

          inner class MyRendererTrackingWebViewClient : WebViewClient() {
              private var mWebView: WebView? = null

              override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
                  if (!detail.didCrash()) {
                      // Renderer was killed because the system ran out of memory.
                      // The app can recover gracefully by creating a new WebView instance
                      // in the foreground.
                      Log.e("MY_APP_TAG", ("System killed the WebView rendering process " +
                          "to reclaim memory. Recreating..."))

                      mWebView?.also { webView ->
                          val webViewContainer: ViewGroup = findViewById(R.id.my_web_view_container)
                          webViewContainer.removeView(webView)
                          webView.destroy()
                          mWebView = null
                      }

                      // By this point, the instance variable "mWebView" is guaranteed
                      // to be null, so it's safe to reinitialize it.

                      return true // The app continues executing.
                  }

                  // Renderer crashed because of an internal error, such as a memory
                  // access violation.
                  Log.e("MY_APP_TAG", "The WebView rendering process crashed!")

                  // In this example, the app itself crashes after detecting that the
                  // renderer crashed. If you choose to handle the crash more gracefully
                  // and allow your app to continue executing, you should 1) destroy the
                  // current WebView instance, 2) specify logic for how the app can
                  // continue executing, and 3) return "true" instead.
                  return false
              }
          }

### Renderer Importance API
- Hiện giờ các webview ở chế độ operate in multiprocess mode, bạn có thế linh hoạt trong ứng dụng của bạn xử lý các tình huống khi hết bộ nhớ. Bạn có thể sử dụng Renderer Importance API, từ Android 8.0 để set độ ưu tiên việc render assigned cho webview. Cụ thể, bạn cps thể muốn phần chính của ứng dụng tiếp tục thực hiện việc render khi webview bị kill. 

- Nếu muốn không hiển thị đối tượng WebView trong một thời gian dài để hệ thống có thể lấy lại bộ nhớ mà trình render 

          val myWebView: WebView = ...
          myWebView.setRendererPriorityPolicy(RENDERER_PRIORITY_BOUND, true)

### Migrating to WebView in Android 4.4

- Android 4.4 giới thiệu new version webview được base từ Chrominum. Nó thay đổi update webview performace và support  cho HTML5, CSS3 và JS phù hợp với các trình duyệt mới nhất. Any app sử dụng webview sẽ sử dụng chúng từ 4.4 trở lên
### User Agent Changes
- nếu server content có user agentm, nó sẽ thay đổi 1 chút trên Chrome 

          Mozilla/5.0 (Linux; Android 4.4; Nexus 4 Build/KRT16H) AppleWebKit/537.36
          (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36
          
* Nếu muốn truy xuất user agent ko lưu trong app hoặc ko muốn khởi tạo sử dụng getDefaultAgent(), nhưng nếu muốn override user agent trong webview sử dụng getUserAgentString()

### Multi-Threading and Thread Blocking
- Nếu bạn sử dụng method trong webview từ bất kỳ một luồng nào khác ngoài main thread, nó có thể gây ra 1 kết quả ko mong muốn. Ví dụ nếu app bạn sử dụng quá nhiều luồng, bạn nên sử dụng runOnUIThread() để đảm bảo nó chạy trên luồng chính 

          runOnUiThread {
              // Code for WebView goes here
          }
 - Phải chắc chắn rằng ko bh bị block thread. Có 1 tình huống bị như này khi chờ callback JS
 
          webView.loadUrl("javascript:fn()")
          while (result == null) {
              Thread.sleep(100)
          }
          
- Thay vào đó có thể sử dụng evaluateJavaScrpit() để chạy JavaScript không đồng bộ

### Custom URL handling
- Cái mới WebViewáp dụng các hạn chế bổ sung khi yêu cầu tài nguyên và giải quyết các liên kết sử dụng lược đồ custom URL. Ví dụ: nếu bạn triển khai callbacl như shouldOverrideUrlLoading()hoặc shouldInterceptRequest()sau đó chỉ WebViewgọi chúng cho các URL hợp lệ.

Nếu bạn đang sử dụng custom URL hoặc base URL và nhận thấy rằng ứng dụng của bạn sẽ nhận được ít cuộc gọi hơn đến các call back này hoặc không tải tài nguyên trên Android 4.4, hãy đảm bảo rằng các yêu cầu chỉ định URL hợp lệ phù hợp với RFC 3986 .

Ví dụ: cái mới WebView có thể không gọi method shouldOverrideUrlLoading() của bạn cho các liên kết như thế này:
 
          <a href="showProfile">Show Profile</a>
          
- Kết quả người dùng khi nhấp vào link như vậy có thể khác nhau
- Nếu bạn để load trang bằng cách gọi loadData() hoặc loadDataWithBaseURL() với base URL ko hợp lệ hoặc ko có giá trị, ta sẽ ko nhận dduocj call back shouldOverrideUrlLoading() khi link trang 
          
- Lưu ý: Khi bạn sử dụng loadDataWithBaseURL()và base URL không hợp lệ hoặc đặt null, tất cả các liên kết trong nội dung bạn đang tải phải tuyệt đối.

- Nếu bạn đã tải trang bằng cách gọi loadUrl() hoặc cung cấp base URL hợp lệ loadDataWithBaseURL(), thì bạn sẽ nhận được call back shouldOverrideUrlLoading() cho loại liên kết này trên trang, nhưng URL bạn nhận được sẽ tuyệt đối, liên quan đến trang hiện tại. Ví dụ: URL bạn nhận được sẽ "http://www.example.com/showProfile"thay vì chỉ "showProfile".

- Thay vì sử dụng một chuỗi đơn giản trong một liên kết như được hiển thị ở trên, bạn có thể sử dụng một lược đồ tùy chỉnh như sau: 

          <a href="example-app:showProfile">Show Profile</a>
          
- Sau đó, bạn có thể xử lý URL này trong shouldOverrideUrlLoading() 

          // The URL scheme should be non-hierarchical (no trailing slashes)
          const val APP_SCHEME = "example-app:"

          override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
              return if (url?.startsWith(APP_SCHEME) == true) {
                  urlData = URLDecoder.decode(url.substring(APP_SCHEME.length), "UTF-8")
                  respondToData(urlData)
                  true
              } else {
                  false
              }
          }
### WebSetting

- Quản lý trạng thái cài đặt cho WebView. Khi một WebView được tạo lần đầu tiên, nó sẽ có được một tập hợp các default setting. Các cài đặt mặc định này sẽ được trả về từ bất kỳ cuộc gọi getter nào. Một WebSettings đthu được từ WebView getSettings()gắn liền với vòng đời của WebView. Nếu một WebView đã bị hủy, bất kỳ lệnh gọi phương thức nào WebSettingscũng sẽ ném IllegalStateException.

- Một số setting hay dùng
- WebSettings.LayoutAlgorithm : Enum để kiểm soát bố cục của html.
              
              NORMALcó nghĩa là không rendering change. Đây là lựa chọn được đề xuất để tương thích tối đa trên các nền tảng và phiên bản Android khác nhau.
          SINGLE_COLUMN di chuyển tất cả nội dung vào một cột có chiều rộng của view
          NARROW_COLUMNSlàm cho tất cả các cột không rộng hơn màn hình nếu có thể. Chỉ sử dụng điều này cho các cấp API trước Build.VERSION_CODES.KITKAT.
          TEXT_AUTOSIZINGtăng kích thước phông chữ của các đoạn văn dựa trên phương pháp phỏng đoán để làm cho văn bản có thể đọc được khi xem bố cục khung nhìn rộng ở chế độ tổng quan. Bạn nên bật hỗ trợ thu phóng WebSettings.setSupportZoom(boolean)khi sử dụng chế độ này. Được hỗ trợ từ cấp API Build.VERSION_CODES.KITKAT

- WebSettings.ZoomDensity : chỉ định mật độ zoom mong muốn 
                   
                   FAR makes 100% looking like in 240dpi
                    MEDIUM makes 100% looking like in 160dpi
                    CLOSE makes 100% looking like in 120dpi
                    
- setAllowContentAccess() :  cho phép WebView này có hỗ trợ truy cập content URL không.
- setAllowFileAccess() : cho phép Webview truy cập file hay ko 
- setAllowFileAccessFromFileURLs() : liệu JavaScript có chạy trong context của 1 file URL có thể access nội dụng của file khác trên URL ko???
- setAllowUniversalAccessFromFileURLs(): liệu JavaScript có chạy trong context của 1 file URL có thể accessnội dụng của bất kì orgin 
- setBlockNetworkImage() : set cho phép webview tải image từ network
- setBlockNetworkLoads() :  set cho phép webview tải resource từ network
- setBuiltInZoomControls() : set các cơ chế zoom mà webview đang sử dụng 
- setCacheMode: cơ chế cache của webview

          LOAD_CACHE_ELSE_NETWORK : Sử dụng tài nguyên được lưu trong bộ nhớ cache khi chúng có sẵn, ngay cả khi chúng đã hết hạn. Nếu không thì tải tài nguyên từ mạng.
          LOAD_CACHE_ONLY : ko dùng mạng chỉ lấy từ cache
          LOAD_DEFAULT : Chế độ sử dụng bộ đệm mặc định. Nếu loại điều hướng không áp đặt bất kỳ hành vi cụ thể nào, hãy sử dụng các tài nguyên được lưu trong bộ nhớ cache khi chúng có sẵn và không hết hạn, nếu không thì tải tài nguyên từ mạng.
          LOAD_NORMAL : derpecated from 17
          LOAD_NO_CACHE : đ dùng cache
          
- setDatabaseEnabled() : Xem bật DB api hay ko
- setJavaScriptEnabled() : bật JS hay ko 
- setLoadWithOverviewMode() : WebView có tải các trang ở chế độ tổng quan hay không, nghĩa là thu nhỏ nội dung để vừa với màn hình theo chiều rộng.

### Java Script interfacer
- Nhằm mục đích giao tiếp giữa client và server

          public class WebAppInterface {
              Context mContext;

              // Instantiate the interface and set the context
              WebAppInterface(Context c) {
                  mContext = c;
              }

              // Show a toast from the web page
              @JavascriptInterface
              public void showToast(String toast) {
                  Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
              }

              @JavascriptInterface
              public int getAndroidVersion() {
                  return android.os.Build.VERSION.SDK_INT;
              }

              @JavascriptInterface
              public void showAndroidVersion(String versionName) {
                  Toast.makeText(mContext, versionName, Toast.LENGTH_SHORT).show();
              }

          }

      

          public class WebViewActivity extends AppCompatActivity {

              @Override
              protected void onCreate(Bundle savedInstanceState) {
                  super.onCreate(savedInstanceState);
                  setContentView(R.layout.activity_webview);

                  WebView webView = (WebView) findViewById(R.id.webview);
                  webView.loadUrl("file:///android_asset/index.html");

                  webView.addJavascriptInterface(new WebAppInterface(this), "AndroidInterface"); // To call methods in Android from using js in the html, AndroidInterface.showToast, AndroidInterface.getAndroidVersion etc
                  WebSettings webSettings = webView.getSettings();
                  webSettings.setJavaScriptEnabled(true);
                  webView.setWebViewClient(new MyWebViewClient());
                  webView.setWebChromeClient(new MyWebChromeClient());
              }


              private class MyWebViewClient extends WebViewClient {
                  @Override
                  public void onPageFinished (WebView view, String url) {
                      //Calling a javascript function in html page
                      view.loadUrl("javascript:alert(showVersion('called by Android'))");
                  }
              }

              private class MyWebChromeClient extends WebChromeClient {
                  @Override
                  public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                      Log.d("LogTag", message);
                      result.confirm();
                      return true;
                  }
              }
          }
          
               <!DOCTYPE html>
          <html lang="en">
            <head>
              <meta charset="utf-8">
              <meta http-equiv="X-UA-Compatible" content="IE=edge">
              <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">

              <title>Hello</title>

              <style>
                body, html {
                  height: 100%;
                  text-align: center;
                  display: flex;
                  justify-content: center;
                  align-items: center;
                  color: #F89821;
                  background-color: #ffffff;
                  padding: 20px;
                  margin-bottom: 100px;
                }
              </style>

            </head>
            <body>
                <input type="button" value="Say hello" onClick="showAndroidToast('Hello Android!')" />
                <br/><br/>
                <input type="button" value="Show Version" onClick="showVersion('called within the html')" />
                <br/><br/>
                <p id="version"></p>
                <script type="text/javascript">
                  <!-- Sending value to Android -->
                  function showAndroidToast(toast) {
                      AndroidInterface.showToast(toast);
                  }

                  <!-- Getting value from Android -->
                  function showVersion(msg) {
                      var myVar = AndroidInterface.getAndroidVersion();
                      document.getElementById("version").innerHTML = msg + " You are running API Version " + myVar;
                  }
                </script>
            </body>
          </html>     
