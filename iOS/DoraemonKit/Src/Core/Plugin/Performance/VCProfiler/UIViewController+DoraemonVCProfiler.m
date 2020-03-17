//
//  UIViewController+DoraemonVCProfiler.m
//  DoraemonKit-DoraemonKit
//
//  Created by didi on 2020/1/5.
//

#import "UIViewController+DoraemonVCProfiler.h"
#import "NSObject+Doraemon.h"
#import <objc/runtime.h>
#import "DoraemonHealthManager.h"
#import "DoraemonCacheManager.h"

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
    [[DoraemonCacheManager sharedInstance] saveShouldAutorotate:kvo_self.shouldAutorotate];
    
    void (*func)(UIViewController *, SEL) = (void (*)(UIViewController *, SEL))origin_imp;

    //VCLog(@"VC: %p -viewDidLoad \t\tbegin  at CF time:\t%lf", kvo_self, CFAbsoluteTimeGetCurrent());
    //VCLog(@"yixiang class = %@ viewDidLoad",origin_cls);
    func(kvo_self, _sel);
    //VCLog(@"VC: %p -viewDidLoad \t\tfinish at CF time:\t%lf", kvo_self, CFAbsoluteTimeGetCurrent());
}

static void doraemon_vc_profiler_viewWillAppear(UIViewController *kvo_self, SEL _sel, BOOL animated) {
    Class kvo_cls = object_getClass(kvo_self);
    Class origin_cls = class_getSuperclass(kvo_cls);

    IMP origin_imp = method_getImplementation(class_getInstanceMethod(origin_cls, _sel));
    assert(origin_imp != NULL);

    void (*func)(UIViewController *, SEL, BOOL) = (void (*)(UIViewController *, SEL, BOOL))origin_imp;

    //VCLog(@"yixiang class = %@ viewWillAppear",origin_cls);
    //VCLog(@"VC: %p -viewWillAppear \tbegin  at CF time:\t%lf", kvo_self, CFAbsoluteTimeGetCurrent());
    func(kvo_self, _sel, animated);
    //VCLog(@"VC: %p -viewWillAppear \tfinish at CF time:\t%lf", kvo_self, CFAbsoluteTimeGetCurrent());
}

static void doraemon_vc_profiler_viewDidAppear(UIViewController *kvo_self, SEL _sel, BOOL animated) {
    Class kvo_cls = object_getClass(kvo_self);
    Class origin_cls = class_getSuperclass(kvo_cls);
    IMP origin_imp = method_getImplementation(class_getInstanceMethod(origin_cls, _sel));
    assert(origin_imp != NULL);

    void (*func)(UIViewController *, SEL, BOOL) = (void (*)(UIViewController *, SEL, BOOL))origin_imp;

    //VCLog(@"yixiang class = %@ viewDidAppear",origin_cls);
    //VCLog(@"VC: %p -viewDidAppear \tbegin  at CF time:\t%lf", kvo_self, CFAbsoluteTimeGetCurrent());
    func(kvo_self, _sel, animated);
    //VCLog(@"VC: %p -viewDidAppear \tfinish at CF time:\t%lf", kvo_self, CFAbsoluteTimeGetCurrent());
    [[DoraemonHealthManager sharedInstance] enterPage:origin_cls];
    [[DoraemonCacheManager sharedInstance] saveShouldAutorotate:kvo_self.shouldAutorotate];
}

static void doraemon_vc_profiler_viewWillDisAppear(UIViewController *kvo_self, SEL _sel, BOOL animated) {
    Class kvo_cls = object_getClass(kvo_self);
    Class origin_cls = class_getSuperclass(kvo_cls);
    IMP origin_imp = method_getImplementation(class_getInstanceMethod(origin_cls, _sel));
    assert(origin_imp != NULL);

    void (*func)(UIViewController *, SEL, BOOL) = (void (*)(UIViewController *, SEL, BOOL))origin_imp;

    //VCLog(@"yixiang class = %@ viewWillDisAppear",origin_cls);
    //VCLog(@"VC: %p -viewDidAppear \tbegin  at CF time:\t%lf", kvo_self, CFAbsoluteTimeGetCurrent());
    func(kvo_self, _sel, animated);
    //VCLog(@"VC: %p -viewDidAppear \tfinish at CF time:\t%lf", kvo_self, CFAbsoluteTimeGetCurrent());
}

static void doraemon_vc_profiler_viewDidDisappear(UIViewController *kvo_self, SEL _sel, BOOL animated) {
    Class kvo_cls = object_getClass(kvo_self);
    Class origin_cls = class_getSuperclass(kvo_cls);
    IMP origin_imp = method_getImplementation(class_getInstanceMethod(origin_cls, _sel));
    assert(origin_imp != NULL);

    void (*func)(UIViewController *, SEL, BOOL) = (void (*)(UIViewController *, SEL, BOOL))origin_imp;

    //VCLog(@"yixiang class = %@ viewDidDisappear",origin_cls);
    //VCLog(@"VC: %p -viewDidAppear \tbegin  at CF time:\t%lf", kvo_self, CFAbsoluteTimeGetCurrent());
    func(kvo_self, _sel, animated);
    //VCLog(@"VC: %p -viewDidAppear \tfinish at CF time:\t%lf", kvo_self, CFAbsoluteTimeGetCurrent());
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
    //VCLog(@"target == %@ , dealloc",_target);
    [_target removeObserver:[DoraemonFakeKVOObserver shared] forKeyPath:_keyPath];
    _target = nil;
}

@end

@implementation UIViewController (DoraemonVCProfiler)

+ (void)load {
    [self doraemon_swizzleInstanceMethodWithOriginSel:@selector(initWithNibName:bundle:) swizzledSel:@selector(doraemon_initWithNibName:bundle:)];
    [self doraemon_swizzleInstanceMethodWithOriginSel:@selector(initWithCoder:) swizzledSel:@selector(doraemon_initWithCoder:)];
    [self doraemon_swizzleInstanceMethodWithOriginSel:@selector(shouldAutorotate) swizzledSel:@selector(doraemon_shouldAutorotate)];
}
- (BOOL)doraemon_shouldAutorotate{
    return [[DoraemonCacheManager sharedInstance] shouldAutorotate];
}

- (instancetype)doraemon_initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil{
    [self createAndHookKVOClass];
    //VCLog(@"vc(initWithNibName) ==  %@",[self class]);
    [self doraemon_initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    return self;
}

- (instancetype)doraemon_initWithCoder:(NSCoder *)coder{
    [self createAndHookKVOClass]; 
    //VCLog(@"vc(initWithCoder) == %@",[self class]);
    [self doraemon_initWithCoder:coder];
    return self;
}

- (void)createAndHookKVOClass {
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
    
    //VCLog(@"Hook %@", kvoCls);
    
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
