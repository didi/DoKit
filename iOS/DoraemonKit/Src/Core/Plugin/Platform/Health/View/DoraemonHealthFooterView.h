//
//  DoraemonHealthFooterView.h
//  AFNetworking
//
//  Created by didi on 2020/1/1.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol DoraemonHealthFooterButtonDelegate<NSObject>

- (void)footerBtnClick:(id)sender;

@end

@interface DoraemonHealthFooterView : UIView


@property (nonatomic, weak) id<DoraemonHealthFooterButtonDelegate> delegate;
@property (nonatomic, assign) BOOL top;

- (void)renderUIWithTitleImg:(NSString *)title img:(NSString *)imgName;
- (void)renderUIWithTitleImg:(BOOL)top;

@end

NS_ASSUME_NONNULL_END
