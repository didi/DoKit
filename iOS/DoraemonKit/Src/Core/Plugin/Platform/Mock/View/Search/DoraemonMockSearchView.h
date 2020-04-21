//
//  DoraemonMockSearchView.h
//  AFNetworking
//
//  Created by didi on 2019/10/23.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol DoraemonMockSearchViewDelegate  <NSObject>

- (void)searchViewInputChange:(NSString *)text;

@end

@interface DoraemonMockSearchView : UIView

@property (nonatomic, strong) UITextField *textField;
@property (nonatomic, weak) id<DoraemonMockSearchViewDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
