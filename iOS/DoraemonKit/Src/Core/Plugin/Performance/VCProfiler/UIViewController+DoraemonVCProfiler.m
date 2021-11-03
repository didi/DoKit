//
//  UIViewController+DoraemonVCProfiler.m
//  DoraemonKit
//
//  Created by didi on 2020/1/5.
//

#import "UIViewController+DoraemonVCProfiler.h"
#import "NSObject+Doraemon.h"
#import "DoraemonDefine.h"
#import <objc/runtime.h>
#import "DoraemonHealthManager.h"
#import "DoraemonCacheManager.h"
#import "DoraemonManager.h"

//#define Doraemon_VC_Profiler_LOG_ENABLE 

#ifdef Doraemon_VC_Profiler_LOG_ENABLE
#define VCLog(...) NSLog(__VA_ARGS__)
#else
#define VCLog(...)
#endif

static char const kAssociatedRemoverKey;
static NSString *const kUniqueFakeKeyPath = @"doraemon_vc_profiler_key_path";

#pragma mark - IMP of Key Method

static void doraemon_vc_profiler_viewDidLoad(UIViewController *kvo_self, SEL _sel) {
    Class kvo_cls = object_getClass(kvo_self);
    Class origin_cls = class_getSuperclass(kvo_cls);
    IMP origin_imp = method_getImplementation(class_getInstanceMethod(origin_cls, _sel));
    assert(origin_imp != NULL);

    [[DoraemonHealthManager sharedInstance] startEnterPage:origin_cls];
    
    void (*func)(UIViewController *, SEL) = (void (*)(UIViewController *, SEL))origin_imp;
    func(kvo_self, _sel);
}

static void doraemon_vc_profiler_viewWillAppear(UIViewController *kvo_self, SEL _sel, BOOL animated) {
    Class kvo_cls = object_getClass(kvo_self);
    Class origin_cls = class_getSuperclass(kvo_cls);

    IMP origin_imp = method_getImplementation(class_getInstanceMethod(origin_cls, _sel));
    assert(origin_imp != NULL);

    void (*func)(UIViewController *, SEL, BOOL) = (void (*)(UIViewController *, SEL, BOOL))origin_imp;
    func(kvo_self, _sel, animated);
}

static void doraemon_vc_profiler_viewDidAppear(UIViewController *kvo_self, SEL _sel, BOOL animated) {
    Class kvo_cls = object_getClass(kvo_self);
    Class origin_cls = class_getSuperclass(kvo_cls);
    IMP origin_imp = method_getImplementation(class_getInstanceMethod(origin_cls, _sel));
    assert(origin_imp != NULL);

    void (*func)(UIViewController *, SEL, BOOL) = (void (*)(UIViewController *, SEL, BOOL))origin_imp;
    
    func(kvo_self, _sel, animated);
    [[DoraemonHealthManager sharedInstance] enterPage:origin_cls];
}

static void doraemon_vc_profiler_viewWillDisAppear(UIViewController *kvo_self, SEL _sel, BOOL animated) {
    Class kvo_cls = object_getClass(kvo_self);
    Class origin_cls = class_getSuperclass(kvo_cls);
    IMP origin_imp = method_getImplementation(class_getInstanceMethod(origin_cls, _sel));
    assert(origin_imp != NULL);

    void (*func)(UIViewController *, SEL, BOOL) = (void (*)(UIViewController *, SEL, BOOL))origin_imp;

    func(kvo_self, _sel, animated);
}

static void doraemon_vc_profiler_viewDidDisappear(UIViewController *kvo_self, SEL _sel, BOOL animated) {
    Class kvo_cls = object_getClass(kvo_self);
    Class origin_cls = class_getSuperclass(kvo_cls);
    IMP origin_imp = method_getImplementation(class_getInstanceMethod(origin_cls, _sel));
    assert(origin_imp != NULL);

    void (*func)(UIViewController *, SEL, BOOL) = (void (*)(UIViewController *, SEL, BOOL))origin_imp;

    func(kvo_self, _sel, animated);
    [[DoraemonHealthManager sharedInstance] leavePage:origin_cls];
}


@interface DoraemonFakeKVOObserver : NSObject

@end

@implementation DoraemonFakeKVOObserver

+ (instancetype)shared{
    static id sharedInstance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [[self alloc] init];
    });
    return sharedInstance;
}

@end

@interface DoraemonFakeKVORemover : NSObject

@property (nonatomic, unsafe_unretained) id target;
@property (nonatomic, copy) NSString *keyPath;

@end

@implementation DoraemonFakeKVORemover

- (void)dealloc{
    //DoKitLog(@"target == %@ , dealloc",_target);
    [_target removeObserver:[DoraemonFakeKVOObserver shared] forKeyPath:_keyPath];
    _target = nil;
}

@end

@implementation UIViewController (DoraemonVCProfiler)

+ (void)load {
    if ([[DoraemonCacheManager sharedInstance] healthStart]){
        [self doraemon_swizzleInstanceMethodWithOriginSel:@selector(initWithNibName:bundle:) swizzledSel:@selector(doraemon_initWithNibName:bundle:)];
        [self doraemon_swizzleInstanceMethodWithOriginSel:@selector(initWithCoder:) swizzledSel:@selector(doraemon_initWithCoder:)];
    }
}

- (instancetype)doraemon_initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil{
    [self createAndHookKVOClass];
    //DoKitLog(@"vc(initWithNibName) ==  %@",[self class]);
    [self doraemon_initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    return self;
}

- (instancetype)doraemon_initWithCoder:(NSCoder *)coder{
    [self createAndHookKVOClass]; 
    //DoKitLog(@"vc(initWithCoder) == %@",[self class]);
    [self doraemon_initWithCoder:coder];
    return self;
}

// 黑名单用户不会触发KVO监控
- (BOOL)blackList:(NSString *)className{
    NSArray *blackList=[DoraemonManager shareInstance].vcProfilerBlackList;
    if (blackList && blackList.count>0 && [blackList containsObject:className]) {
       return YES;
    }
    return NO;
}

- (void)createAndHookKVOClass {
    if ([self blackList:NSStringFromClass(self.class)]) {
        return;
    }
    [self addObserver:[DoraemonFakeKVOObserver shared] forKeyPath:kUniqueFakeKeyPath options:NSKeyValueObservingOptionNew context:nil];
    
    DoraemonFakeKVORemover *remover = [[DoraemonFakeKVORemover alloc] init];
    remover.target = self;
    remover.keyPath = kUniqueFakeKeyPath;
    objc_setAssociatedObject(self, &kAssociatedRemoverKey, remover, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
    
    //NSKVONotifying_ViewController
    Class kvoCls = object_getClass(self);
    
    IMP currentViewDidLoadImp = class_getMethodImplementation(kvoCls, @selector(viewDidLoad));
    if (currentViewDidLoadImp == (IMP)doraemon_vc_profiler_viewDidLoad) {
        return;
    }
    
    Class originCls = class_getSuperclass(kvoCls);
    
    //DoKitLog(@"Hook %@", kvoCls);
    
    // 获取原来实现的encoding
    const char *originViewDidLoadEncoding = method_getTypeEncoding(class_getInstanceMethod(originCls, @selector(viewDidLoad)));
    const char *originViewWillAppearEncoding = method_getTypeEncoding(class_getInstanceMethod(originCls, @selector(viewWillAppear:)));
    const char *originViewDidAppearEncoding = method_getTypeEncoding(class_getInstanceMethod(originCls, @selector(viewDidAppear:)));
    const char *originViewWillDisappearEncoding = method_getTypeEncoding(class_getInstanceMethod(originCls, @selector(viewWillDisappear:)));
    const char *originViewDidDisappearEncoding = method_getTypeEncoding(class_getInstanceMethod(originCls, @selector(viewDidDisappear:)));
    
    // 重点，添加方法。
    class_addMethod(kvoCls, @selector(viewDidLoad), (IMP)doraemon_vc_profiler_viewDidLoad, originViewDidLoadEncoding);
    class_addMethod(kvoCls, @selector(viewDidAppear:), (IMP)doraemon_vc_profiler_viewDidAppear, originViewDidAppearEncoding);
    class_addMethod(kvoCls, @selector(viewWillAppear:), (IMP)doraemon_vc_profiler_viewWillAppear, originViewWillAppearEncoding);
    class_addMethod(kvoCls, @selector(viewWillDisappear:), (IMP)doraemon_vc_profiler_viewWillDisAppear, originViewWillDisappearEncoding);
    class_addMethod(kvoCls, @selector(viewDidDisappear:),  (IMP)doraemon_vc_profiler_viewDidDisappear, originViewDidDisappearEncoding);
}

@end
