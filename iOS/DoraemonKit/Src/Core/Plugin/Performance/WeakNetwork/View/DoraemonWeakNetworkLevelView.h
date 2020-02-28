//
//  DoraemonWeakNetworkLevelView.h
//  AFNetworking
//
//  Created by didi on 2019/12/16.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol DoraemonWeakNetworkLevelViewDelegate<NSObject>

- (void)segmentSelected:(NSInteger)index;

@end

@interface DoraemonWeakNetworkLevelView : UIView

@property (nonatomic, weak) id<DoraemonWeakNetworkLevelViewDelegate> delegate;

-(void)renderUIWithItemArray:(NSArray *)itemArray selecte:(NSUInteger)selected;

@end

NS_ASSUME_NONNULL_END
