//
//  RCTPermissionMapManger.m
//  RCTPermissionManager
//
//  Created by MFHJ-DZ-001-417 on 16/5/12.
//  Copyright © 2016年 MFHJ-DZ-001-417. All rights reserved.
//

#import "RCTPermissionMapManger.h"
#import <CoreLocation/CoreLocation.h>
#import <UIKit/UIKit.h>

@interface RCTPermissionMapManger ()<CLLocationManagerDelegate>

@property (nonatomic ,strong) CLLocationManager *mgr;
@property (nonatomic,assign)BOOL MapAllow;

@end

@implementation RCTPermissionMapManger
-(instancetype)init
{
    if (self=[super init]) {
        
        self.mgr.delegate = self;
        
        if([[UIDevice currentDevice].systemVersion doubleValue] >= 8.0)
        {
            NSLog(@"是iOS8");
            // 主动要求用户对我们的程序授权, 授权状态改变就会通知代理
        }else
        {
            NSLog(@"是iOS7");
            // 3.开始监听(开始获取位置) IOS7之前默认都是允许进行定位的
            self.bolock(@"IOS系统小于等于7.0 地图设置默认都是开启的");
        }
    }
    return self;
}
#pragma mark - 懒加载
- (CLLocationManager *)mgr
{
    if (!_mgr) {
        _mgr = [[CLLocationManager alloc] init];
    }
    return _mgr;
}

- (void)locationManager:(CLLocationManager *)manager didChangeAuthorizationStatus:(CLAuthorizationStatus)status
{
    
    if (status == kCLAuthorizationStatusNotDetermined) {
        NSLog(@"等待用户授权");
        
        self.bolock(@"地图授权失败");
        
    }else if (status == kCLAuthorizationStatusAuthorizedAlways ||
              status == kCLAuthorizationStatusAuthorizedWhenInUse)
        
    {
        NSLog(@"授权成功");
        // 开始定位
        self.bolock(@"地图授权成功");
        
    }else
    {
        NSLog(@"授权失败");
        self.bolock(@"地图授权失败");
        
    }
}

@end
