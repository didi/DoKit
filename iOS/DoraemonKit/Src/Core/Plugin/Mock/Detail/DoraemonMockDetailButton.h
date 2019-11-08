//
//  DoraemonMockDetailButton.h
//  AFNetworking
//
//  Created by didi on 2019/11/4.
//

#import <UIKit/UIKit.h>
#import "DoraemonMockDetailModel.h"

@protocol DoraemonMockDetailButtonDelegate<NSObject>

- (void)detailBtnClick:(id)sender;

@end

@interface DoraemonMockDetailButton : UIView

@property (nonatomic, weak) id<DoraemonMockDetailButtonDelegate> delegate;

@property (nonatomic, assign) BOOL isSelected;

- (void) needImage;

- (void) renderTitle:(NSString *)title  isSelected:(BOOL)select;

- (void) didSelected;

- (void) cancelSelected;

@end

