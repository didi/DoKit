//
//  DoraemonHomeCell.h
//  DoraemonKit
//
//  Created by dengyouhua on 2019/9/4.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonHomeCell : UICollectionViewCell

- (void)update:(NSString *)image name:(NSString *)name;
- (void)updateImage:(UIImage *)image;

@end

NS_ASSUME_NONNULL_END
