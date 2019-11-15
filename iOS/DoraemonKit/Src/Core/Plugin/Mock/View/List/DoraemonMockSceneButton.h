//
//  DoraemonMockDetailButton.h
//  AFNetworking
//
//  Created by didi on 2019/11/4.
//

#import <UIKit/UIKit.h>

@protocol DoraemonMockSceneButtonDelegate<NSObject>

- (void)sceneBtnClick:(NSInteger)tag;

@end

@interface DoraemonMockSceneButton : UIView

@property (nonatomic, weak) id<DoraemonMockSceneButtonDelegate> delegate;

@property (nonatomic, assign) BOOL isSelected;

- (void) renderTitle:(NSString *)title  isSelected:(BOOL)select;

- (void) didSelected;

- (void) cancelSelected;

+ (CGFloat)viewWidth:(NSString *)sceneName;

@end

