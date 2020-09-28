//
//  DoraemonDemoWebViewController.m
//  DoraemonKitDemo
//
//  Created by 刘骁阳 on 2020/9/27.
//  Copyright © 2020 yixiang. All rights reserved.
//

#import "DoraemonDemoWebViewController.h"
#import <WebKit/WebKit.h>
#import <DoraemonKit/DoraemonAppInfoUtil.h>
#import <DoraemonKit/UIColor+Doraemon.h>

static WKProcessPool *_sharedProcessPool() {
    static WKProcessPool *pool = nil;
    
    if (!pool) {
        pool = [[WKProcessPool alloc] init];
    }
    return pool;
}


@interface DoraemonDemoWebViewController () <WKNavigationDelegate, WKUIDelegate>

@property (strong, nonatomic) WKWebView *webView;
@property (strong, nonatomic) UIProgressView *progressView;

@end

@implementation DoraemonDemoWebViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    self.title = DoraemonDemoLocalizedString(@"h5助手测试");

    WKWebViewConfiguration *config = [[WKWebViewConfiguration alloc] init];
    if (@available(iOS 10.0, *)) {
        config.dataDetectorTypes = WKDataDetectorTypeAll;
    }
    
    CGFloat topOffset = ([DoraemonAppInfoUtil isIPhoneXSeries] ? 88 : 64);
    CGFloat bottomOffset = ([DoraemonAppInfoUtil isIPhoneXSeries] ? 32 : 0);
    CGRect webViewFrame = CGRectMake(0, topOffset, [UIScreen mainScreen].bounds.size.width, [UIScreen mainScreen].bounds.size.height-topOffset-bottomOffset);
    WKWebView *webView = [[WKWebView alloc] initWithFrame:webViewFrame configuration:config];
    config.processPool = _sharedProcessPool();
    webView.allowsBackForwardNavigationGestures = YES;
    webView.UIDelegate = self;
    webView.navigationDelegate = self;
    
    if (@available(iOS 9.0, *)) {
        webView.allowsLinkPreview = NO;
    }
    if (@available(iOS 11.0, *)) {
        webView.scrollView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    }
    else {
        self.automaticallyAdjustsScrollViewInsets = NO;
    }
    
    [webView addObserver:self
              forKeyPath:@"estimatedProgress"
                 options:NSKeyValueObservingOptionInitial | NSKeyValueObservingOptionNew
                 context:nil];
    [self.view addSubview:webView];

    self.webView = webView;
    
    CGRect frame = self.navigationController.navigationBar.frame;
    UIProgressView *progressView = [[UIProgressView alloc] initWithFrame:CGRectMake(0, frame.size.height - 2, frame.size.width, 2)];
    progressView.progressTintColor = UIColor.doraemon_blue;
    progressView.trackTintColor = [UIColor clearColor];
    progressView.progress = 0.0f;
    progressView.hidden = YES;
    [self.navigationController.navigationBar addSubview:progressView];
    self.progressView = progressView;
    
    if ([self.url.scheme hasPrefix:@"file"]) {
        NSString *html = [NSString stringWithContentsOfFile:self.url.path encoding:NSUTF8StringEncoding error:nil];
        [self.webView loadHTMLString:html baseURL:self.url];
    }
    else {
        NSURLRequest *req = [[NSURLRequest alloc] initWithURL:self.url];
        [self.webView loadRequest:req];
    }

}

- (void)viewWillDisappear:(BOOL)animated {
    [super viewWillDisappear:animated];
    
    self.progressView.hidden = YES;
}

- (void)dealloc {
    [self.webView removeObserver:self forKeyPath:@"estimatedProgress"];
}

- (void)backButtonClicked:(UIButton *)backButton {
    if ([self.webView canGoBack]) {
        [self.webView goBack];
    }
    else {
        [self closeButtonClicked:nil];
    }
}

- (void)closeButtonClicked:(nullable UIButton *)closeButton {
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark - WKNavigationDelegate

- (void)webView:(WKWebView *)webView decidePolicyForNavigationAction:(WKNavigationAction *)navigationAction decisionHandler:(void (^)(WKNavigationActionPolicy))decisionHandler {
    if (decisionHandler) {
        decisionHandler(WKNavigationActionPolicyAllow);
    }
}

- (void)webView:(WKWebView *)webView didStartProvisionalNavigation:(null_unspecified WKNavigation *)navigation {
    self.webView.opaque = YES;
}

- (void)webView:(WKWebView *)webView decidePolicyForNavigationResponse:(WKNavigationResponse *)navigationResponse decisionHandler:(void (^)(WKNavigationResponsePolicy))decisionHandler {
    if (decisionHandler) {
        decisionHandler(WKNavigationResponsePolicyAllow);
    }
}

- (void)webView:(WKWebView *)webView didFailProvisionalNavigation:(null_unspecified WKNavigation *)navigation withError:(NSError *)error {
    NSLog(@"#[DoraemonDemo]: didFailProvisionalNavigation errorCode=%@, msg=%@", @(error.code), error.localizedDescription);
}

- (void)webView:(WKWebView *)webView didCommitNavigation:(null_unspecified WKNavigation *)navigation {
    
}

- (void)webView:(WKWebView *)webView didFinishNavigation:(null_unspecified WKNavigation *)navigation {
    //如果网页加载过快，会出现一闪的情况，延迟一下，可以避免
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        webView.opaque = NO;
    });
}

- (void)webView:(WKWebView *)webView didFailNavigation:(null_unspecified WKNavigation *)navigation withError:(NSError *)error {
    NSLog(@"#[DoraemonDemo]: didFailNavigation errorCode=%@, msg=%@", @(error.code), error.localizedDescription);
}

- (void)webViewWebContentProcessDidTerminate:(WKWebView *)webView API_AVAILABLE(macosx(10.11), ios(9.0)) {
    //bugly踩的坑， 解决白屏问题
    if ([webView.URL.scheme hasPrefix:@"file"]) {
        NSString *html = [NSString stringWithContentsOfFile:webView.URL.path encoding:NSUTF8StringEncoding error:nil];
        [webView loadHTMLString:html baseURL:webView.URL];
    }
    else {
        [webView reload];
    }
}

#pragma mark - WKUIDelegate

- (void)webView:(WKWebView *)webView runJavaScriptAlertPanelWithMessage:(NSString *)message initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(void))completionHandler {
    UIAlertController *controller = [UIAlertController alertControllerWithTitle:@""
                                                                        message:message
                                                                 preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *action = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        completionHandler();
    }];
    [controller addAction:action];
    [self presentViewController:controller animated:YES completion:nil];
}

- (void)webView:(WKWebView *)webView runJavaScriptConfirmPanelWithMessage:(NSString *)message initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(BOOL result))completionHandler {
    UIAlertController *controller = [UIAlertController alertControllerWithTitle:nil
                                                                        message:message
                                                                 preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *action = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        completionHandler(YES);
    }];
    [controller addAction:action];
    UIAlertAction *cancle = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        completionHandler(NO);
    }];
    [controller addAction:cancle];
    [self presentViewController:controller animated:YES completion:nil];
}

- (void)webView:(WKWebView *)webView runJavaScriptTextInputPanelWithPrompt:(NSString *)prompt defaultText:(nullable NSString *)defaultText initiatedByFrame:(WKFrameInfo *)frame completionHandler:(void (^)(NSString * _Nullable result))completionHandler {
    UIAlertController *controller = [UIAlertController alertControllerWithTitle:prompt
                                                                        message:@""
                                                                 preferredStyle:UIAlertControllerStyleAlert];
    [controller addTextFieldWithConfigurationHandler:^(UITextField * _Nonnull textField) {
        textField.text = defaultText;
    }];
    UIAlertAction *action = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        completionHandler(controller.textFields.lastObject.text);
    }];
    [controller addAction:action];
    [self presentViewController:controller animated:YES completion:nil];
}

#pragma mark - KVO webView progress

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary<NSString *,id> *)change context:(void *)context {
    if ([keyPath isEqualToString:@"estimatedProgress"]) {
        self.progressView.progress = self.webView.estimatedProgress;
        self.progressView.hidden = (fabs(self.webView.estimatedProgress - 1.0f) < 0.000001);
    }
    else {
        [super observeValueForKeyPath:keyPath ofObject:object change:change context:context];
    }
}


@end
