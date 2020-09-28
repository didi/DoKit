//
//  DoraemonWebHelperViewController.h
//  AFNetworking
//
//  Created by 刘骁阳 on 2020/9/27.
//

#import "DoraemonBaseViewController.h"
#import <WebKit/WebKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonWebHelperViewController : DoraemonBaseViewController

@property (nonatomic, weak) WKWebView *webView;

@end

NS_ASSUME_NONNULL_END
