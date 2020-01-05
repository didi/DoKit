//
//  DoraemonHealthHomeView.h
//  AFNetworking
//
//  Created by didi on 2020/1/2.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void (^DoraemonHealthHomeBlock)(void);

@interface DoraemonHealthHomeView : UIView

- (void)addBlock:(DoraemonHealthHomeBlock)block;

@end

NS_ASSUME_NONNULL_END
