//
//  DoraemonKitManagerCell.h
//  AFNetworking
//
//  Created by didi on 2020/4/28.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonKitManagerCell : UICollectionViewCell

- (void)update:(NSString *)image name:(NSString *)name select:(BOOL)select editStatus:(BOOL)editStatus;

@end

NS_ASSUME_NONNULL_END
