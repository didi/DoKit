//
//  DoraemonMCServer.m
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//

#import "DoraemonMCServer.h"
#import <CocoaHTTPServer/HTTPServer.h>
#import <CocoaHTTPServer/HTTPConnection.h>
#import <CocoaHTTPServer/WebSocket.h>
#import <CocoaHTTPServer/HTTPMessage.h>
#import <CocoaHTTPServer/HTTPDynamicFileResponse.h>
#import <CocoaAsyncSocket/GCDAsyncSocket.h>
#import <DoraemonKit/DoraemonToastUtil.h>
#import "DoraemonHomeWindow.h"

NSInteger const kDoraemonMCServerPort = 8088;

@interface MyWebSocket : WebSocket

@end

@interface MyHttpConnection : HTTPConnection
@property (nonatomic , strong) MyWebSocket *wsInstance;
@end


@interface DoraemonMCServer ()
@property (nonatomic , strong) HTTPServer *server;
@end

@implementation DoraemonMCServer

+ (instancetype)shareInstance {
   static  DoraemonMCServer *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

+ (BOOL)startServerWithError:(NSError *__autoreleasing  _Nullable *)error {
    return [[self shareInstance] startServerWithError:error];
}

+ (void)close {
    [[self shareInstance] close];
}

+ (NSInteger)connectCount {
    return [[self shareInstance] connectCount];
}

- (BOOL)startServerWithError:(NSError *__autoreleasing  _Nullable *)error  {
    if (!self.server) {
        self.server = [[HTTPServer alloc] init];
        [self.server setConnectionClass:[MyHttpConnection class]];
        [self.server setType:@"_http._tcp."];
        [self.server setPort:kDoraemonMCServerPort];
        NSError *errorP = nil;
        if (![self.server start:&errorP]) {
            NSLog(@"%@",errorP);
            [self.server stop];
            self.server = nil;
            if (error != NULL) {
                *error = errorP;
            }
            return NO;
        }else {
            return YES;
        }
    }
    return YES;
}

- (void)close {
    if (self.server) {
        [self.server stop];
        self.server = nil;
        UIWindow *currentWindow = nil;
        if ([DoraemonHomeWindow shareInstance].hidden) {
            currentWindow = [UIApplication sharedApplication].keyWindow;
        }else {
            currentWindow = [DoraemonHomeWindow shareInstance];
        }
        [DoraemonToastUtil showToastBlack:@"服务已关闭" inView:currentWindow];
    }
}

- (void)sendMessage:(NSString *)message {
    [[self.server valueForKey:@"webSockets"] enumerateObjectsUsingBlock:^(MyWebSocket  *_Nonnull ws, NSUInteger idx, BOOL * _Nonnull stop) {
        [ws sendMessage:message];
    }];
}

- (NSInteger)connectCount {
    return [[self.server valueForKey:@"webSockets"] count];
}

+ (void)sendMessage:(NSString *)message {
    [[self shareInstance] sendMessage:message];
}


+ (BOOL)isOpen {
    return [[self shareInstance] server].isRunning;
}

@end


@implementation MyWebSocket

- (void)didOpen {
    [super didOpen];
    NSLog(@"didOpen");
}

- (void)didReceiveMessage:(NSString *)msg {
    NSLog(@"didReceiveMessage : %@" , msg);
}

- (void)didClose {
    [super didClose];
    NSLog(@"didClose");
}

@end


@implementation MyHttpConnection

- (NSObject<HTTPResponse> *)httpResponseForMethod:(NSString *)method URI:(NSString *)path {
    if ([path isEqualToString:@"/wsConnect"]) {
        NSString *wsHost = [request headerField:@"Host"];
        NSString *wsLocation = nil;
        if (wsHost == nil)
        {
            NSString *port = [NSString stringWithFormat:@"%hu", [asyncSocket localPort]];
            wsLocation = [NSString stringWithFormat:@"ws://localhost:%@/service", port];
        }
        else
        {
            wsLocation = [NSString stringWithFormat:@"ws://%@/service", wsHost];
        }
        return [[HTTPDynamicFileResponse alloc] initWithFilePath:[self filePathForURI:path] forConnection:self separator:@"%%" replacementDictionary:@{@"WEBSOCKET_URL" : wsLocation}];
    }
    return [super httpResponseForMethod:method URI:path];
}


- (WebSocket *)webSocketForURI:(NSString *)path {
    if ([path isEqualToString:@"/MyWs"]) {
        self.wsInstance = [[MyWebSocket alloc] initWithRequest:request socket:asyncSocket];
        return self.wsInstance;
    }
    return [super webSocketForURI:path];
}

@end
