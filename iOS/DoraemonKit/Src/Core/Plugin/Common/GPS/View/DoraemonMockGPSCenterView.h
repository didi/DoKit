//
//  DoraemonMockGPSCenterView.h
//  AFNetworking
//
//  Created by yixiang on 2018/12/2.
//

#import <UIKit/UIKit.h>


@interface DoraemonMockGPSCenterView : UIView

- (void)renderUIWithGPS:(NSString *)gps;

- (void)hiddenGPSInfo:(BOOL)hidden;

@end

