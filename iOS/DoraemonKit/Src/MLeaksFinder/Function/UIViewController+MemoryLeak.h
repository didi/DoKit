//
//  UIViewController+MemoryLeak.h
//  MLeaksFinder
//
//  Created by zeposhe on 12/12/15.
//  Copyright © 2015 zeposhe. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MLeaksFinder.h"

#if _INTERNAL_MLF_ENABLED

@interface UIViewController (MemoryLeak)

@end

#endif
