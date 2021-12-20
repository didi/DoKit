//
//  DoraemonNSLogSearchView.h
//  DoraemonKit
//
//  Created by yixiang on 2018/12/3.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol DoraemonNSLogSearchViewDelegate  <NSObject>

- (void)searchViewInputChange:(NSString *)text;

@end

@interface DoraemonNSLogSearchView : UIView

@property (nonatomic, weak) id<DoraemonNSLogSearchViewDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
