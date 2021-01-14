//
//  MLeaksFinder.h
//  MLeaksFinder
//
//  Created by zeposhe on 12/12/15.
//  Copyright © 2015 zeposhe. All rights reserved.
//

#import "NSObject+MemoryLeak.h"

//#define MEMORY_LEAKS_FINDER_ENABLED 0

#ifdef MEMORY_LEAKS_FINDER_ENABLED
#define _INTERNAL_MLF_ENABLED MEMORY_LEAKS_FINDER_ENABLED
#else
#define _INTERNAL_MLF_ENABLED DEBUG
#endif

#define MEMORY_LEAKS_FINDER_RETAIN_CYCLE_ENABLED 0

#if MEMORY_LEAKS_FINDER_RETAIN_CYCLE_ENABLED
#define _INTERNAL_MLF_RC_ENABLED MEMORY_LEAKS_FINDER_RETAIN_CYCLE_ENABLED
#elif __has_include(<FBRetainCycleDetector/FBRetainCycleDetector.h>)
#define _INTERNAL_MLF_RC_ENABLED 1
#endif
