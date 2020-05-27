//
//  DoraemonPageTimeProfiler.m
//  DoraemonKit
//
//  Created by Frank on 2020/5/27.
//

#import "DoraemonPageTimeProfiler.h"

#import "NSObject+Doraemon.h"
#import "DoraemonPageTimeInstance.h"
#import <objc/runtime.h>
#import "DoraemonTimeProfiler.h"

static char const kAssociatedKey;
static NSString *const kUniqueFakeKeyPath = @"doraemon_useless_key_path";
static void (*orig_sel)(id _self, SEL _sel);
static void (*orig_animated_sel)(id _self, SEL _sel, BOOL animated);

static inline void doraemon_timeProfiler(id _self, SEL _sel) {
    Class kvoCls = object_getClass(_self);
    Class orginCls = class_getSuperclass(kvoCls);
    IMP orginImp = method_getImplementation(class_getInstanceMethod(orginCls, _sel));
    orig_sel = (void (*)(id _self, SEL _sel))orginImp;
    [DoraemonTimeProfiler startRecord];
    orig_sel(_self, _sel);
    [DoraemonTimeProfiler stopRecord];
    [[DoraemonPageTimeInstance sharedInstance] timeWithVC:_self sel:_sel];
}

static inline void doraemon_animated_timeProfiler(id _self, SEL _sel, BOOL animated) {
    Class kvoCls = object_getClass(_self);
    Class orginCls = class_getSuperclass(kvoCls);
    IMP orginImp = method_getImplementation(class_getInstanceMethod(orginCls, _sel));
    orig_animated_sel = (void (*)(id _self, SEL _sel, BOOL animated))orginImp;
    [DoraemonTimeProfiler startRecord];
    orig_animated_sel(_self, _sel, animated);
    [DoraemonTimeProfiler stopRecord];
    [[DoraemonPageTimeInstance sharedInstance] timeWithVC:_self sel:_sel];
}


static inline void doraemon_viewDidLayoutSubviews(id _self, SEL _sel) {
    doraemon_timeProfiler(_self, _sel);
}

static inline void doraemon_loadView(id _self, SEL _sel) {
    doraemon_timeProfiler(_self, _sel);
}

static inline void doraemon_viewDidload(id _self, SEL _sel) {
    doraemon_timeProfiler(_self, _sel);
}

static inline void doraemon_viewWillAppear(id _self, SEL _sel, BOOL animated) {
    doraemon_animated_timeProfiler(_self, _sel, animated);
}

static void doraemon_viewDidAppear(id _self, SEL _sel, BOOL animated) {
    doraemon_animated_timeProfiler(_self, _sel, animated);
}

@interface doraemonTimeProfilerKVORemover : NSObject

@property (nonatomic, unsafe_unretained) id target;
@property (nonatomic, copy) NSString *keyPath;

@end

@implementation doraemonTimeProfilerKVORemover

- (void)dealloc {
    @try {
        [_target removeObserver:[DoraemonPageTimeInstance sharedInstance]
                     forKeyPath:_keyPath];
    } @catch(NSException *e) {
        NSLog(@"%s: %@", __func__, e);
    }
    _target = nil;
}

@end

@implementation UIViewController (TimeProfiller)

+ (void)load {
    [self swizzleMethodInClass];
}

+ (void)swizzleMethodInClass {
    [self doraemon_swizzleInstanceMethodWithOriginSel:@selector(initWithNibName:bundle:) swizzledSel:@selector(doraemon_initWithNibName:bundle:)];
    
    [self doraemon_swizzleInstanceMethodWithOriginSel:@selector(initWithCoder:) swizzledSel:@selector(doraemon_initWithCoder:)];
    
    [self doraemon_swizzleInstanceMethodWithOriginSel:@selector(init) swizzledSel:@selector(doraemon_init)];
}


- (instancetype)doraemon_initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    [self addLifeCycleTOKVOClass];
    return [self doraemon_initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
}

- (instancetype)doraemon_initWithCoder:(NSCoder *)coder {
    [self addLifeCycleTOKVOClass];
    return [self doraemon_initWithCoder:coder];
}

- (instancetype)doraemon_init
{
    [self addLifeCycleTOKVOClass];
    return [self doraemon_init];
}

- (void)addLifeCycleTOKVOClass {
    
    if ([self check]) {
        return;
    }
    
    [self addObserver:DoraemonPageTimeInstance.sharedInstance forKeyPath:kUniqueFakeKeyPath options:NSKeyValueObservingOptionNew context:nil];
    
    // 内存管理
    doraemonTimeProfilerKVORemover *obj = [doraemonTimeProfilerKVORemover new];
    obj.target = self;
    obj.keyPath = kUniqueFakeKeyPath;
    objc_setAssociatedObject(self, &kAssociatedKey, obj, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    
    Class kvoCls = object_getClass(self);
    IMP curViewDidLayoutSubviews = class_getMethodImplementation(kvoCls, @selector(viewDidLayoutSubviews));
    if (curViewDidLayoutSubviews == (IMP)doraemon_viewDidLayoutSubviews) {
        return;
    }
    
    const char *orginViewDidLayoutSubviews = method_getTypeEncoding(class_getInstanceMethod(self.class, @selector(viewDidLayoutSubviews)));
    const char *orginLoadView = method_getTypeEncoding(class_getInstanceMethod(self.class, @selector(loadView)));
    const char *orginViewWillAppear = method_getTypeEncoding(class_getInstanceMethod(self.class, @selector(viewWillAppear:)));
    const char *orginViewDidLoad = method_getTypeEncoding(class_getInstanceMethod(self.class, @selector(viewDidLoad)));
    const char *orginViewDidAppear = method_getTypeEncoding(class_getInstanceMethod(self.class, @selector(viewDidAppear:)));

    class_addMethod(kvoCls, @selector(viewDidLayoutSubviews), (IMP)doraemon_viewDidLayoutSubviews, orginViewDidLayoutSubviews);
    class_addMethod(kvoCls, @selector(loadView), (IMP)doraemon_loadView, orginLoadView);
    class_addMethod(kvoCls, @selector(viewDidAppear:), (IMP)doraemon_viewDidAppear, orginViewDidAppear);
    class_addMethod(kvoCls, @selector(viewDidLoad), (IMP)doraemon_viewDidload, orginViewDidLoad);
    class_addMethod(kvoCls, @selector(viewWillAppear:), (IMP)doraemon_viewWillAppear, orginViewWillAppear);
}

- (BOOL)check {
    // 过滤掉默认系统UI类
    NSString *className = NSStringFromClass([self class]);
    if ([className hasPrefix:@"UI"]) {
        return YES;
    }
    
    return NO;
}
@end

