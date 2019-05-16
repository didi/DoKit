//
//  DoraemonLargeImageDetectionManager.m
//  DoraemonKit
//
//  Created by licd on 2019/5/15.
//

#import "DoraemonLargeImageDetectionManager.h"
#import "DoraemonCacheManager.h"
#import "DoraemonResponseImageModel.h"

static DoraemonLargeImageDetectionManager *instance = nil;

@implementation DoraemonLargeImageDetectionManager
    
-(NSMutableArray<DoraemonResponseImageModel *> *)images {
    if (_images == nil) {
        _images = [NSMutableArray array];
    }
    return _images;
}

+ (instancetype)shareInstance {
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        instance = [[DoraemonLargeImageDetectionManager alloc] init];
    });
    return instance;
}
    // todo:
- (void)setIsListening:(BOOL)isListening {
    _isListening = isListening;
    [[DoraemonCacheManager sharedInstance] saveLargeImageDetectionSwitch: isListening];
}
    
- (void)handle:(NSURLResponse *)response {
    if (![response.MIMEType hasPrefix:@"image/"]) {
        return;
    }
    DoraemonResponseImageModel *model = [[DoraemonResponseImageModel alloc] initWithResponse: response];
    [self.images addObject: model];
}
    
@end
