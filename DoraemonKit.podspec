# Be sure to run `pod lib lint DoraemonKit.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see https://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'DoraemonKit'
  s.version          = '3.1.7'
  s.summary          = 'iOS 各式各样的工具集合'

# This description is used to generate tags and improve search results.
#   * Think: What does it do? Why did you write it? What is the focus?
#   * Try to keep it short, snappy and to the point.
#   * Write the description between the DESC delimiters below.
#   * Finally, don't worry about the indent, CocoaPods strips it!

  s.description      = <<-DESC
iOS各式各样的工具集合
                       DESC

  s.homepage         = 'https://www.dokit.cn'
  # s.screenshots     = 'www.example.com/screenshots_1', 'www.example.com/screenshots_2'
  s.license          = { :type => 'Apache-2.0', :file => 'LICENSE' }
  s.author           = { 'OrangeLab' => 'orange-lab@didiglobal.com' }
  s.source           = { :git => 'https://github.com/didi/DoraemonKit.git', :tag => s.version.to_s }
  # s.social_media_url = 'https://twitter.com/<TWITTER_USERNAME>'

  s.ios.deployment_target = '9.0'

  s.default_subspec = 'Core'
  s.pod_target_xcconfig = {
    'DEFINES_MODULE' => 'YES'
  }
  
  s.subspec 'CFoundation' do |ss|
    ss.source_files = 'iOS/DoKit/Classes/CFoundation/*.{h,c}'
    ss.compiler_flags = '-Wall', '-Wextra', '-Wpedantic', '-Werror', '-fvisibility=hidden'
  end
  
  s.subspec 'Foundation' do |ss|
    ss.source_files = 'iOS/DoKit/Classes/Foundation/**/*.{h,m}'
    # language-extension-token warning be used to implement Objective-C typeof().
    # ?: grammar
    ss.compiler_flags = '-Wall', '-Wextra', '-Werror'
    ss.dependency 'SocketRocket', '~> 0.6'
    ss.dependency 'Mantle', '~> 2.2'
  end
  
#  s.subspec 'CoreNG' do |ss|
#    ss.dependency 'DoraemonKit/Foundation'
#    ss.source_files = 'iOS/DoKit/Classes/Core/**/*.{h,m}'
#    # language-extension-token warning be used to implement Objective-C typeof().
#    # ?: grammar
#    ss.compiler_flags = '-Wall', '-Wextra', '-Wpedantic', '-Werror', '-Wno-language-extension-token', '-Wno-gnu-conditional-omitted-operand'
#    ss.resource_bundle = {
#      'DoKitResource' => [
#        'iOS/DoKit/Assets/Assets.xcassets',
#        'iOS/DoKit/Assets/*.xib'
#      ]
#    }
#    ss.dependency 'SocketRocket', '~> 0.6'
#    ss.dependency 'Mantle', '~> 2.2'
#  end

  s.subspec 'EventSynthesize' do |ss|
    ss.source_files = 'iOS/DoKit/Classes/EventSynthesize/*.{h,m}'
    ss.compiler_flags = '-Wall', '-Wextra', '-Wpedantic', '-Werror', '-fvisibility=hidden', '-Wno-gnu-conditional-omitted-operand', '-Wno-pointer-arith','-Wno-gnu-zero-variadic-macro-arguments', '-Wno-unused-variable'
    ss.framework = 'IOKit'
    ss.dependency 'DoraemonKit/Foundation'
    ss.dependency 'DoraemonKit/CFoundation'
  end

  s.subspec 'Core' do |ss| 
    ss.source_files = 'iOS/DoraemonKit/Src/Core/**/*.{h,m,c,mm}'
    ss.resource_bundle = {
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
    # https://guides.cocoapods.org/syntax/podspec.html#vendored_frameworks
    # TODO(ChasonTang): Should change to vendored_framework?
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
    # FBRetainCycleDetector 存在编译问题，使用pod发布时lint不通过，因此采用壳工程配置方式引入
    #ss.dependency 'FBRetainCycleDetector'
  end

  s.subspec 'WithMultiControl' do |ss|
    ss.source_files = 'iOS/DoraemonKit/Src/MultiControl/**/*{.h,.m}'
    ss.pod_target_xcconfig = {
      'GCC_PREPROCESSOR_DEFINITIONS' => '$(inherited) DoraemonWithMultiControl'
    }
    ss.dependency 'DoraemonKit/Core'
    ss.dependency 'DoraemonKit/Foundation'
  end
end
