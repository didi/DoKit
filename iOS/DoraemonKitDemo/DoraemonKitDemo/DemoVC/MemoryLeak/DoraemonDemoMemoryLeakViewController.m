//
//  DoraemonDemoMemoryLeakViewController.m
//  DoraemonKitDemo
//
//  Created by didi on 2019/10/6.
//  Copyright © 2019 yixiang. All rights reserved.
//

#import "DoraemonDemoMemoryLeakViewController.h"
#import "DoraemonDemoMemoryLeakModel.h"
#import "DoraemonDemoMemoryLeakView.h"
#import "DoraemonKitDemoi18Util.h"

@interface DoraemonDemoMemoryLeakViewController ()

@property (nonatomic, strong) DoraemonDemoMemoryLeakModel *model;
@property (nonatomic, strong) DoraemonDemoMemoryLeakView *testview;

@end

@implementation DoraemonDemoMemoryLeakViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonDemoLocalizedString(@"内存泄漏测试");
    
    //_model = [[DoraemonDemoMemoryLeakModel alloc] init];
//    [_model addBlock:^{
//        [self printXX];
//    }];
    //[_model install];

    _testview = [[DoraemonDemoMemoryLeakView alloc] initWithFrame:CGRectMake(100, 200, 100, 100)];
    [self.view addSubview:_testview];
    
    __weak id weakSelf = self;
    //NSLog(@"self == %@",self);
    //NSLog(@"weakSelf == %@",weakSelf);
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        __strong id strongSelf = weakSelf;
        NSLog(@"strongSelf == %@",strongSelf);
        [strongSelf assertNotDeallocXX];
    });
    
    /*
    * weakself的缺陷，可能会导致内存提前回收
    */
       /*
        这么做和直接用self有什么区别，为什么不会有循环引用：外部的weakSelf是为了打破环，从而使得没有循环引用，
        
        而内部的strongSelf仅仅是个局部变量，存在栈中，会在block执行结束后回收，不会再造成循环引用。
        
        这里的strongSelf会使 BlockLeakViewController 的对象引用计数＋1，使得BlockLeakViewController pop到 上个controller 的时候，并不会执行dealloc，因为引用计数还不为0，
        
        strongSelf仍持有BlockLeakViewController，而在block执行完，局部的strongSelf才会回收，此时BlockLeakViewController dealloc。
        
    那block中的StrongSelf又是做什么的呢？还是上面的例子，当你加了WeakSelf后，block中的self随时都会有被释放的可能，所以会出现一种情况，在调用doSomething的时候self还存在，在doMoreThing的时候self就变成nil了，所以为了避免这种情况发生，我们会重新strongify self。一般情况下，我们都建议这么做，这没什么风险，除非你不关心self在执行过程中变成nil，或者你确定它不会变成nil（比方说所以block都在main thread执行）。
        
        */
    
    //https://www.jianshu.com/p/51bb714051ea https://www.jianshu.com/p/ae4f84e289b9
    
}

- (void)assertNotDeallocXX{
    NSLog(@"assertNotDealloc");
}

- (void)viewDidDisappear:(BOOL)animated{
    [super viewDidDisappear:animated];
    
    //__weak id weakSelf = self;
//    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(4 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
//        //__strong id strongSelf = weakSelf;
//        [self printXX];
//    });
}

- (void)printXX{
    NSLog(@"XX");
}

- (void)dealloc{
    NSLog(@"DoraemonDemoMemoryLeakViewController dealloc");
}

@end
