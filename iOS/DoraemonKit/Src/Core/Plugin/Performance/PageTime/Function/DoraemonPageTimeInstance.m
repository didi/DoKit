//
//  DoraemonPageTimeInstance.m
//  DoraemonKit
//
//  Created by Frank on 2020/5/27.
//

#import "DoraemonPageTimeInstance.h"

#import "DoraemonTimeProfiler.h"
#import "DoraemonHealthManager.h"

@implementation GKTZeusPageTimeRecord
@end

@interface DoraemonPageTimeInstance ()
@property (nonatomic, strong) NSMutableDictionary *pageTimeDict;
@property (nonatomic, strong) dispatch_queue_t pageTimeQueue;
@property (nonatomic, strong) NSMutableArray *arrayRecord;
@end

@implementation DoraemonPageTimeInstance

static DoraemonPageTimeInstance *_sharedInstance = nil;

#pragma mark - life cycle
+ (instancetype)sharedInstance {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        //because has rewrited allocWithZone  use NULL avoid endless loop lol.
        _sharedInstance = [[super allocWithZone:NULL] init];
        _sharedInstance.pageTimeDict = [NSMutableDictionary dictionary];
        _sharedInstance.pageTimeQueue = dispatch_queue_create("doraemon_page_time_queue", DISPATCH_QUEUE_SERIAL);
        _sharedInstance.arrayRecord = [NSMutableArray array];
    });

    return _sharedInstance;
}

- (NSArray *)getArrayRecord {
    return self.arrayRecord;
}

- (void)timeWithVC:(id)vc sel:(SEL)sel {
    
    dispatch_async(self.pageTimeQueue, ^{
        NSString *detail =  DoraemonHealthManager.sharedInstance.costDetail;
        NSTimeInterval f = [DoraemonTimeProfiler totalTime];

        NSString *p = [NSString stringWithFormat:@"%p",vc];
        GKTZeusPageTimeRecord *record = self.pageTimeDict[p];
        
        // 如果是空并且方法是loadView则代表当前页面第一次进入
        if (!record && !strcmp(sel_getName(sel), "loadView")) {
            record = [GKTZeusPageTimeRecord new];
            record.clsName = NSStringFromClass([vc class]);
            record.loadViewTime = f;
            record.loadViewTimeDict = detail;
            [self.pageTimeDict setObject:record forKey:p];
        } else if (record && !strcmp(sel_getName(sel), "viewDidLoad")) {
            record.viewDidLoadTime = f;
            record.viewDidLoadTimeDict = detail;
        } else if (record && !strcmp(sel_getName(sel), "viewWillAppear:")) {
            record.viewWillAppearTime = f;
            record.viewWillAppearTimeDict = detail;
        } else if (record && !strcmp(sel_getName(sel), "viewDidAppear:")) {
            record.viewWillDidAppearTime = f;
            record.viewWillDidAppearTimeDict = detail;
            [self.arrayRecord addObject:record];
            [self.pageTimeDict removeAllObjects];
        } else if (record && !strcmp(sel_getName(sel), "viewDidLayoutSubviews")) {
            record.viewDidLayoutSubviewsTime = f;
            record.viewDidLayoutSubviewsTimeDict = detail;
        }
    });
}

+ (id)allocWithZone:(struct _NSZone *)zone {
    return [DoraemonPageTimeInstance sharedInstance];
}

+ (instancetype)alloc {
    return [DoraemonPageTimeInstance sharedInstance];
}

- (id)copy {
    return self;
}

- (id)mutableCopy {
    return self;
}

- (id)copyWithZone:(struct _NSZone *)zone {
    return self;
}

#ifdef DEBUG
- (void)dealloc {
    NSLog(@"%s", __func__);
}
#endif

@end
