//
//  DoraemonHomeHeadCell.h
//  DoraemonKit
//
//  Created by dengyouhua on 2019/9/4.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonHomeHeadCell : UICollectionReusableView

- (void)renderUIWithTitle:(nullable NSString *)title;
- (void)renderUIWithSubTitle:(NSString *)subTitle;

@end

NS_ASSUME_NONNULL_END
