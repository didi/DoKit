# Make sure to run `pod lib lint DoraemonKit.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'DoraemonKit'
  s.version          = '3.0.7'
  s.summary          = 'iOS各式各样的工具集合'
  s.description      = <<-DESC
                          iOS各式各样的工具集合 Desc
                       DESC

  s.homepage         = 'https://github.com/didi/DoraemonKit'
  s.license          = { :type => 'Apache-2.0', :file => 'LICENSE' }
  s.author           = { 'yixiang' => 'javer_yi@163.com' }
  s.source           = { :git => 'https://github.com/didi/DoraemonKit.git', :tag => s.version.to_s }
  s.ios.deployment_target = '9.0'

  s.user_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }
  s.pod_target_xcconfig = { 'EXCLUDED_ARCHS[sdk=iphonesimulator*]' => 'arm64' }

  s.default_subspec = 'Core'
  
  s.subspec 'Core' do |ss| 
    ss.source_files = 'iOS/DoraemonKit/Src/Core/**/*{.h,.m,.c,.mm}'
    ###ss.vendored_frameworks = 'DoraemonKit/Lib/CrashReporter.framework'
    ss.resource_bundles = {
      'DoraemonKit' => 'iOS/DoraemonKit/Resource/**/*'
    }
    ss.dependency 'GCDWebServer'
    ss.dependency 'GCDWebServer/WebUploader'
    ss.dependency 'GCDWebServer/WebDAV'
    ss.dependency 'FMDB'
  end

  s.subspec 'WithLogger' do |ss| 
    ss.source_files = 'iOS/DoraemonKit/Src/Logger/**/*{.h,.m}'
    ss.pod_target_xcconfig = {
      'GCC_PREPROCESSOR_DEFINITIONS' => '$(inherited) DoraemonWithLogger'
    }
    ss.dependency 'DoraemonKit/Core'
    ss.dependency 'CocoaLumberjack'
  end

  s.subspec 'WithGPS' do |ss| 
    ss.source_files = 'iOS/DoraemonKit/Src/GPS/**/*{.h,.m}'
    ss.pod_target_xcconfig = {
      'GCC_PREPROCESSOR_DEFINITIONS' => '$(inherited) DoraemonWithGPS'
    }
    ss.dependency 'DoraemonKit/Core'
  end

  s.subspec 'WithLoad' do |ss| 
    ss.source_files = 'iOS/DoraemonKit/Src/MethodUseTime/**/*{.h,.m}'
    ss.pod_target_xcconfig = {
      'GCC_PREPROCESSOR_DEFINITIONS' => '$(inherited) DoraemonWithLoad'
    }
    ss.dependency 'DoraemonKit/Core'
    ss.vendored_frameworks = 'iOS/DoraemonKit/Framework/*.framework'
  end

  s.subspec 'WithWeex' do |ss| 
    ss.source_files = 'iOS/DoraemonKit/Src/Weex/**/*{.h,.m}'
    ss.pod_target_xcconfig = {
      'GCC_PREPROCESSOR_DEFINITIONS' => '$(inherited) DoraemonWithWeex'
    }
    ss.dependency 'DoraemonKit/Core'
    ss.dependency 'WeexSDK'
    ss.dependency 'WXDevtool'
  end
  
  s.subspec 'WithDatabase' do |ss|
      ss.source_files = 'iOS/DoraemonKit/Src/Database/**/*{.h,.m}'
      ss.pod_target_xcconfig = {
          'GCC_PREPROCESSOR_DEFINITIONS' => '$(inherited) DoraemonWithDatabase'
      }
      ss.dependency 'DoraemonKit/Core'
      ss.dependency 'YYDebugDatabase'
  end

  s.subspec 'WithMLeaksFinder' do |ss|
    ss.source_files = 'iOS/DoraemonKit/Src/MLeaksFinder/**/*{.h,.m}'
    ss.pod_target_xcconfig = {
      'GCC_PREPROCESSOR_DEFINITIONS' => '$(inherited) DoraemonWithMLeaksFinder'
    }
    ss.dependency 'DoraemonKit/Core'
    ss.dependency 'FBRetainCycleDetector'
  end


    #一机多控
    s.subspec 'WithMultiControl' do |ss|
      ss.source_files = 'iOS/DoraemonKit/Src/MultiControl/**/*{.h,.m}'
      ss.pod_target_xcconfig = {
        'GCC_PREPROCESSOR_DEFINITIONS' => '$(inherited) DoraemonWithMultiControl'
      }
      ss.dependency 'DoraemonKit/Core'
      ss.dependency 'CocoaLumberjack'
      ss.dependency 'CocoaHTTPServer'
    end

end

