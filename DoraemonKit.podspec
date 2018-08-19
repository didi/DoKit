# Make sure to run `pod lib lint KOPNetworking.podspec' to ensure this is a
# valid spec before submitting.
#
# Any lines starting with a # are optional, but their use is encouraged
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#

Pod::Spec.new do |s|
  s.name             = 'DoraemonKit'
  s.version          = '0.0.74'
  s.summary          = 'iOS各式各样的工具集合'
  s.description      = <<-DESC
                          iOS各式各样的工具集合 Desc
                       DESC

  s.homepage         = '上线之后的dorameonKit首页'
  s.license          = { :type => 'MIT', :file => 'LICENSE' }
  s.author           = { 'yixiang' => '上线之后的联系邮箱' }
  s.source           = { :git => '上线之后的dorameonKit首页地址', :tag => s.version.to_s }
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

  s.dependency 'UIView+Positioning'
  s.dependency 'PNChart'

end

