//
//  RunTrace.h
//  RunTrace
//
//  Created by 孙昕 on 15/9/18.
//  Copyright (c) 2015年 孙昕. All rights reserved.
//

#import <UIKit/UIKit.h>

#ifdef DEBUG

#define RunTraceOpen 1

#endif
@interface RunTrace : UIView

- (void)hide;

- (void)show;


@end
