# Make sure to run `pod lib lint KOPNetworking.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'DoraemonKit'
  s.version          = '1.0.1'
  s.summary          = 'iOS各式各样的工具集合'
  s.description      = <<-DESC
                          iOS各式各样的工具集合 Desc
                       DESC

  s.homepage         = 'https://github.com/didi/DoraemonKit'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'yixiang' => 'javer_yi@163.com' }
  s.source           = { :git => 'https://github.com/didi/DoraemonKit', :tag => s.version.to_s }
  s.ios.deployment_target = '8.0'


  s.default_subspec = 'Core'
  
  s.subspec 'Core' do |ss| 
    ss.source_files = 'DoraemonKit/Src/Core/**/*{.h,.m}'
    ###ss.vendored_frameworks = 'DoraemonKit/Lib/CrashReporter.framework'
    ss.resource_bundles = {
      'DoraemonKit' => 'DoraemonKit/Resource/**/*'
    }
  end

  s.subspec 'WithLogger' do |ss| 
    ss.source_files = 'DoraemonKit/Src/Logger/**/*{.h,.m}'
    ss.pod_target_xcconfig = {
      'GCC_PREPROCESSOR_DEFINITIONS' => '$(inherited) DoraemonWithLogger'
    }
    ss.dependency 'DoraemonKit/Core'
    ss.dependency 'CocoaLumberjack'
  end

  s.dependency 'PNChart'

end

