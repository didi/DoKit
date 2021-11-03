//
//  DoraemonWeakNetworkWindow.h
//  DoraemonKit
//
//  Created by didi on 2020/3/21.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@protocol DoraemonWeakNetworkWindowDelegate <NSObject>

- (void)doraemonWeakNetworkWindowClosed;

@end


@interface DoraemonWeakNetworkWindow : UIWindow

+ (DoraemonWeakNetworkWindow *)shareInstance;

- (void)hide;

-(void)updateFlowValue:(NSString *)upFlow downFlow:(NSString *)downFlow fromWeak:(BOOL)is;

// start位置
@property (nonatomic) CGPoint startingPosition;
@property (nonatomic, assign) BOOL upFlowChanged;
@property (nonatomic, assign) BOOL downFlowChanged;
@property (nonatomic, weak) id<DoraemonWeakNetworkWindowDelegate> delegate;

@end

NS_ASSUME_NONNULL_END
