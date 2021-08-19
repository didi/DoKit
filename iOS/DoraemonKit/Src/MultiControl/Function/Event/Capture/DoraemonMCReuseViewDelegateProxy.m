//
//  DoraemonMCReuseViewDelegateProxy.m
//  DoraemonKit
//
//  Created by litianhao on 2021/7/16.
//

#import "DoraemonMCReuseViewDelegateProxy.h"
#import "DoraemonMCServer.h"
#import "DoraemonMCCommandGenerator.h"

@implementation DoraemonMCReuseViewDelegateProxy

+ (instancetype)proxyWithTarget:(id)target {
    return [[self alloc] initWithTarget:target];
}

- (instancetype)initWithTarget:(id)target {
    _target = target ;
    return self;
}


- (id)forwardingTargetForSelector:(SEL)selector {
    return _target;
}

- (void)forwardInvocation:(NSInvocation *)invocation {
    void *null = NULL;
    [invocation setReturnValue:&null];
}

- (NSMethodSignature *)methodSignatureForSelector:(SEL)selector {
    return [NSObject instanceMethodSignatureForSelector:@selector(init)];
}

- (BOOL)respondsToSelector:(SEL)aSelector {
    if ([NSStringFromSelector(aSelector) isEqualToString:NSStringFromSelector(@selector(scrollViewDidEndDecelerating:))]) {
        return YES;
    }
    return [_target respondsToSelector:aSelector];
}

- (BOOL)isEqual:(id)object {
    return [_target isEqual:object];
}

- (NSUInteger)hash {
    return [_target hash];
}

- (Class)superclass {
    return [_target superclass];
}

- (Class)class {
    return [_target class];
}

- (BOOL)isKindOfClass:(Class)aClass {
    return [_target isKindOfClass:aClass];
}

- (BOOL)isMemberOfClass:(Class)aClass {
    return [_target isMemberOfClass:aClass];
}

- (BOOL)conformsToProtocol:(Protocol *)aProtocol {
    return [_target conformsToProtocol:aProtocol];
}

- (BOOL)isProxy {
    return YES;
}

- (NSString *)description {
    return [_target description];
}

- (NSString *)debugDescription {
    return [_target debugDescription];
}


- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    if ([DoraemonMCServer isOpen]) {
        [DoraemonMCCommandGenerator sendMessageWithView:collectionView
                                                gusture:nil
                                                 action:nil
                                              indexPath:indexPath
                                            messageType:DoraemonMCMessageTypeDidSelectCell];
    }
    if ([self.target respondsToSelector:@selector(collectionView:didSelectItemAtIndexPath:)]) {
        [self.target collectionView:collectionView didSelectItemAtIndexPath:indexPath];
    }
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if ([DoraemonMCServer isOpen]) {
        [DoraemonMCCommandGenerator sendMessageWithView:tableView
                                                gusture:nil
                                                 action:nil
                                              indexPath:indexPath
                                            messageType:DoraemonMCMessageTypeDidSelectCell];
    }
    if ([self.target respondsToSelector:@selector(tableView:didSelectRowAtIndexPath:)]) {
        [self.target tableView:tableView didSelectRowAtIndexPath:indexPath];
    }
}

- (void)scrollViewDidEndDecelerating:(UIScrollView *)scrollView {
    if ([DoraemonMCServer isOpen]) {
        NSIndexPath *lastIndexPath = nil;
        if ([scrollView isKindOfClass:[UITableView class]]) {
            UITableView *tableView = (UITableView *)scrollView;
            lastIndexPath = [tableView indexPathsForVisibleRows].lastObject;
        }else  if ([scrollView isKindOfClass:[UICollectionView class]]) {
            UICollectionView *collectionView = (UICollectionView *)scrollView;
            lastIndexPath = [collectionView indexPathsForVisibleItems].lastObject;
        }
        if (lastIndexPath != nil) {
            [DoraemonMCCommandGenerator sendMessageWithView:scrollView
                                                    gusture:nil
                                                     action:nil
                                                  indexPath:lastIndexPath
                                                messageType:DoraemonMCMessageTypeDidScrollToCell];
        }
    }
    if ([self.target respondsToSelector:_cmd]) {
        [self.target scrollViewDidEndDecelerating:scrollView];
    }
}
@end
