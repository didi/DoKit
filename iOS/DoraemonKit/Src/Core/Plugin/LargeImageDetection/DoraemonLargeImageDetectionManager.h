//
//  DoraemonLargeImageDetectionManager.h
//  DoraemonKit
//
//  Created by licd on 2019/5/15.
//

#import <Foundation/Foundation.h>
@class DoraemonResponseImageModel;

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonLargeImageDetectionManager : NSObject
@property (nonatomic, assign) BOOL isListening;
@property (nonatomic, strong) NSMutableArray<DoraemonResponseImageModel *> *images;

+ (instancetype) shareInstance;

//- (void)addModel: (DoraemonResponseImageModel *)model;
- (void)handle: (NSURLResponse *)response;;

@end

NS_ASSUME_NONNULL_END
