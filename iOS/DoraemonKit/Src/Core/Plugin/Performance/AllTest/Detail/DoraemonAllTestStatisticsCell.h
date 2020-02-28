//
//  DoraemonAllTestStatisticsCell.h
//  AFNetworking
//
//  Created by didi on 2019/9/26.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonAllTestStatisticsCell : UICollectionViewCell
- (void)update:(NSString *)title up:(NSString *)upValue down:(NSString *)downValue average:(NSString *)averageValue;
@end

NS_ASSUME_NONNULL_END
