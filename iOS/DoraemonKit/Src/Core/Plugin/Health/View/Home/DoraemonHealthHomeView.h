//
//  DoraemonHealthHomeView.h
//  AFNetworking
//
//  Created by didi on 2020/1/2.
//

#import <UIKit/UIKit.h>
#import "DoraemonHealthBtnView.h"

NS_ASSUME_NONNULL_BEGIN

typedef void (^DoraemonHealthHomeBlock)(void);

@interface DoraemonHealthHomeView : UIView

@property (nonatomic, strong) DoraemonHealthBtnView *btnView;

- (void)addBlock:(DoraemonHealthHomeBlock)block;

@end

NS_ASSUME_NONNULL_END
