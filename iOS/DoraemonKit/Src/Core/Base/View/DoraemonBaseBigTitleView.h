//
//  DoraemonBaseBigTitleView.h
//  DoraemonKit
//
//  Created by yixiang on 2018/12/2.
//

#import <UIKit/UIKit.h>

@protocol DoraemonBaseBigTitleViewDelegate <NSObject>

- (void)bigTitleCloseClick;

@end

@interface DoraemonBaseBigTitleView : UIView

@property (nonatomic, strong) NSString *title;
@property (nonatomic, weak) id<DoraemonBaseBigTitleViewDelegate> delegate;

@end
