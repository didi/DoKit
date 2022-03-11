//
//  DoraemonMCGustureSerializer.m
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/13.
//

#import "DoraemonMCGustureSerializer.h"
#import "UIGestureRecognizer+DoraemonMCSerializer.h"
#import "UIResponder+DoraemonMCSerializer.h"

@implementation DoraemonMCGustureSerializer

+ (NSDictionary *)dictFromGusture:(UIGestureRecognizer *)gusture {
    if (![gusture isKindOfClass:[UIGestureRecognizer class]] ) {
        return nil;
    }
    
    NSMutableDictionary *dictM = [NSMutableDictionary dictionary];
    [dictM addEntriesFromDictionary:[gusture do_mc_serialize_dictionary]];
    [dictM addEntriesFromDictionary:[gusture.view do_mc_serialize_dictionary]];
    return dictM.copy;
}

+ (void)syncInfoToGusture:(UIGestureRecognizer *)gusture withDict:(NSDictionary *)eventInfo {
    [gusture do_mc_serialize_syncInfoWithDictionary:eventInfo];
    [gusture.view do_mc_serialize_syncInfoWithDictionary:eventInfo];
}

@end
