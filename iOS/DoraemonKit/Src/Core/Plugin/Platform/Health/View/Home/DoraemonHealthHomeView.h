//
//  DoraemonHealthHomeView.h
//  DoraemonKit
//
//  Created by didi on 2020/1/2.
//

#import <UIKit/UIKit.h>
#import "DoraemonHealthBtnView.h"
#import "DoraemonHealthStartingTitle.h"

NS_ASSUME_NONNULL_BEGIN

typedef void (^DoraemonHealthHomeBlock)(void);

@interface DoraemonHealthHomeView : UIView

@property (nonatomic, strong) DoraemonHealthBtnView *btnView;
@property (nonatomic, strong) DoraemonHealthStartingTitle *startingTitle;

- (void)addBlock:(DoraemonHealthHomeBlock)block;

@end

NS_ASSUME_NONNULL_END
