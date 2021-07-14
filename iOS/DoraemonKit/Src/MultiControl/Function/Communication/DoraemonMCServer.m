//
//  DoraemonMCServer.m
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//

#import "DoraemonMCServer.h"
#if __has_include(<ONEBus/HTTPServer.h>)
#import <ONEBus/HTTPServer.h>
#import <ONEBus/HTTPConnection.h>
#import <ONEBus/WebSocket.h>
#import <ONEBus/HTTPMessage.h>
#import <ONEBus/HTTPDynamicFileResponse.h>
#else
#import <CocoaHTTPServer/HTTPServer.h>
#import <CocoaHTTPServer/HTTPConnection.h>
#import <CocoaHTTPServer/WebSocket.h>
#import <CocoaHTTPServer/HTTPMessage.h>
#import <CocoaHTTPServer/HTTPDynamicFileResponse.h>
#endif

#import <CocoaAsyncSocket/GCDAsyncSocket.h>
#import <DoraemonKit/DoraemonToastUtil.h>

NSInteger const kDoraemonMCServerPort = 8088;

@interface MyWebSocket : WebSocket

@end

@interface MyHttpConnection : HTTPConnection
@property (nonatomic , strong) MyWebSocket *wsInstance;
@end


@interface DoraemonMCServer ()
@property (nonatomic , strong) HTTPServer *server;
@property (nonatomic , assign) BOOL isServer;
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
            self.isServer = YES;
            return YES;
        }
    }
    return YES;
}

- (void)sendMessage:(NSString *)message {
    [[self.server valueForKey:@"webSockets"] enumerateObjectsUsingBlock:^(MyWebSocket  *_Nonnull ws, NSUInteger idx, BOOL * _Nonnull stop) {
        [ws sendMessage:message];
    }];
}

+ (void)sendMessage:(NSString *)message {
    [[self shareInstance] sendMessage:message];
}

+ (BOOL)isServer {
    return [[self shareInstance] isServer];
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
