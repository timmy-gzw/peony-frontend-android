import groovy.json.JsonSlurper

properties([
        parameters([
                gitParameter(branch: '',
                        branchFilter: 'origin/(.*)',
                        defaultValue: 'main',
                        description: '选择构建分支',
                        name: 'P_BUILD_BRANCH',
                        quickFilterEnabled: true,
                        requiredParameter: true,
                        selectedValue: 'NONE',
                        sortMode: 'NONE',
                        tagFilter: '*',
                        type: 'GitParameterDefinition',
                        useRepository: 'https://codeup.aliyun.com/6288a2dacfea268afc224b5c/peony/peony-frontend-android.git'
                ),
                choice(
                        choices: ['debug', 'release'],
                        description: '''选择构建类型：
1.debug->测试包；
2.release->正式包''',
                        name: 'P_BUILD_TYPE'
                ),
                choice(
                        choices: ['office', 'heart', 'jasmine', 'poinsettia', 'hyacinth'],
                        description: '''选择构建版本：
1.office->伊糖；
2.heart->心心相念；
3.jasmine->甜伴交友；
4.poinsettia->彩糖交友；
5.hyacinth->友缘交友''',
                        name: 'P_BUILD_FLAVOR'
                ),
                booleanParam(
                        description: '是否添加多渠道',
                        name: 'P_MULTI_CHANNEL'
                ),
                booleanParam(
                        description: '是否开启加固',
                        name: 'P_IS_PROTECTION'
                ),
                booleanParam(
                        description: '是否上传到蒲公英',
                        name: 'P_IS_UPLOAD_TO_PGYER'
                )
        ])
])

pipeline {
    agent {
        label 'android'
    }

    environment {
        P_LARK_WEBHOOK = 'https://open.feishu.cn/open-apis/bot/v2/hook/5dfe4993-0b8d-4794-a49b-4ae040a33585'
        P_PGYER_AK = 'e41e5e13a39820ec00368964f8ee7e2c'
        P_PGYER_UK = 'c070ee284e3940d5fb8785d8c18d9ab0'
    }

    stages {
        stage('Prepare') {
            steps {
                script {
                    currentBuild.displayName = "#${BUILD_NUMBER}-${P_BUILD_FLAVOR}-${P_BUILD_TYPE}"
                    currentBuild.description = "构建分支：${P_BUILD_BRANCH}"
                }
            }
        }
        stage("Checkout") {
            steps {
                echo ">>>>>> git checkout <<<<<<<"
                git branch: "$P_BUILD_BRANCH", credentialsId: 'codeup-https', url: 'https://codeup.aliyun.com/6288a2dacfea268afc224b5c/peony/peony-frontend-android.git'
            }
        }

        stage('Build') {
            steps {
                echo ">>>>>> 开始编译 <<<<<<<"
                script {
                    echo "构建分支：${P_BUILD_BRANCH}\n构建版本：${P_BUILD_FLAVOR}\n构建类型：${P_BUILD_TYPE}\n是否多渠道：[${P_MULTI_CHANNEL}]\n是否加固：[${P_IS_PROTECTION}]\n是否上传到蒲公英：[${P_IS_UPLOAD_TO_PGYER}]"
                    sh 'env | grep $HOME'
                    sh 'chmod +x ./gradlew'

                    def versionInfo = getShEchoResult("./gradlew -q printVersion -PinputFlavorName=$P_BUILD_FLAVOR").trim()
                    env.P_VERSION_NAME = versionInfo.split("_")[0]
                    env.P_VERSION_CODE = versionInfo.split("_")[1]

                    sh './gradlew clean && rm -rf ./app/build/'
                    sh "./gradlew assemble${P_BUILD_FLAVOR}${P_BUILD_TYPE} -stacktrace"

                    def getPathCmd = "find ${WORKSPACE}/app/build/outputs/apk/${P_BUILD_FLAVOR}/${P_BUILD_TYPE} -name \"*.apk\" -maxdepth 1 | xargs ls -lta | head -n 1 | awk '{print \$9}'"
                    env.P_APK_PATH = getShEchoResult(getPathCmd)
                    echo ">>> Export Path = [${env.P_APK_PATH}]"
                }
            }
        }

        stage('Protect') {
            when {
                expression {
                    return params.P_IS_PROTECTION && env.P_APK_PATH != null && !(env.P_APK_PATH.toString().isEmpty())
                }
            }
            steps {
                echo ">>>>>> 加固阶段 <<<<<<<"
                script {
                    def configPath = "/yidun/config.ini"
                    def configContent = genYidunConfigText()
                    writeFile(encoding: 'utf-8', file: configPath, text: configContent)
                    echo ">>>>>> 易盾配置信息：<<<<<<"
                    sh "cat ${configPath}"

                    if (P_MULTI_CHANNEL) {
                        def channelPath = "/yidun/channel.txt"
                        writeFile(encoding: 'utf-8', file: channelPath, text: getFullChannels())
                        echo ">>>>>> 多渠道配置信息：<<<<<<"
                        sh "cat ${channelPath}"
                    }

                    echo ">>>>>> 开始加固 <<<<<<<"
                    def yidunJarPath = "/yidun/NHPProtect.jar"
                    if (P_MULTI_CHANNEL) {
                        sh "java -jar $yidunJarPath -yunconfig -fullapk -apksign -zipalign -channel -input ${env.P_APK_PATH}"
                    } else {
                        sh "java -jar $yidunJarPath -yunconfig -fullapk -apksign -zipalign -input ${env.P_APK_PATH}"
                    }
                }
            }
        }

        stage("Publish") {
            when {
                expression {
                    return env.P_APK_PATH != null && !(env.P_APK_PATH.toString().isEmpty())
                }
            }
            steps {
                script {
                    echo ">>>>>> Publish <<<<<<<"
                    if (P_BUILD_TYPE == 'release') {
                        def localApks = getShEchoResult("find app/build/outputs/apk/${P_BUILD_FLAVOR}/${P_BUILD_TYPE} -name '*.apk'")
                        def fileFolder = "${P_BUILD_FLAVOR}${P_BUILD_TYPE}/${env.P_VERSION_NAME}/${getDate()}"
                        def artifactsDir = "/data-oss/$fileFolder"
                        sh "mkdir -p ${artifactsDir}"
                        sh "cp ${localApks} ${artifactsDir}"
                        def ossPrefix = 'https://artifacts-apk.oss-cn-shenzhen.aliyuncs.com/'
                        env.P_FILE_URLS = sh(script: "ls $artifactsDir", returnStdout: true)
                                .trim()
                                .split("\n")
                                .collect { "$ossPrefix$fileFolder/$it" }
                                .join('\n')

                        def mapping = getShEchoResult("find app/build/outputs/mapping -name 'mapping.txt'")
                        sh "cp ${mapping} ${artifactsDir}"
                    } else {
                        if (P_IS_UPLOAD_TO_PGYER) {
                            try {
                                echo ">>>>>> uploading to pgyer <<<<<<<"
                                def pgyerBean = sendToPgyer()
                                env.appShortcutUrl = "https://www.pgyer.com/${pgyerBean.data.appShortcutUrl}"
                                env.appQRCodeURL = pgyerBean.data.appQRCodeURL
                            } catch (e) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                // app/build/outputs/apk/${P_BUILD_FLAVOR}/${P_BUILD_TYPE}/*.apk
                // app/build/outputs/apk/${P_BUILD_FLAVOR}/${P_BUILD_TYPE}/channels/*.apk
                archiveArtifacts allowEmptyArchive: true, artifacts: "app/build/outputs/apk/${P_BUILD_FLAVOR}/${P_BUILD_TYPE}/**/*.apk", caseSensitive: false, followSymlinks: false, onlyIfSuccessful: true
                archiveArtifacts allowEmptyArchive: true, artifacts: "app/build/outputs/mapping/${P_BUILD_FLAVOR}/${P_BUILD_TYPE}/mapping.txt", caseSensitive: false, followSymlinks: false, onlyIfSuccessful: true

                def apkPathExist = env.P_APK_PATH != null && !(env.P_APK_PATH.toString().isEmpty())
                if (apkPathExist) {
                    currentBuild.description = "${currentBuild.description}\n构建产物：\n${env.P_FILE_URLS}"
                }

                def larkTitleAndMsg = genLarkTitleAndMsg(
                        currentBuild.currentResult,
                        currentBuild.durationString.minus(' and counting'),
                        currentBuild.displayName
                )
                def msgContent = genLarkMsg(larkTitleAndMsg[0], larkTitleAndMsg[1], larkTitleAndMsg[2])
                echo msgContent
                sendToLark(msgContent)

                //清理工作空间
//                deleteDir()
            }
        }
    }
}

Object sendToPgyer() {
    def uploadCmd = "curl -F \"file=@${env.P_APK_PATH}\" -F \"uKey=${env.P_PGYER_UK}\" -F \"_api_key=${env.P_PGYER_AK}\" https://www.pgyer.com/apiv1/app/upload"
    def result = getShEchoResult(uploadCmd)
    return new JsonSlurper().parseText(result)
}

void sendToLark(larkContent) {
    sh "curl --location --request POST '${env.P_LARK_WEBHOOK}' --header 'Content-Type: application/json' --data-raw '$larkContent'"
}

// 获取 shell 命令输出内容
void getShEchoResult(cmd) {
    def getShEchoResultCmd = "ECHO_RESULT=`${cmd}`\necho \${ECHO_RESULT}"
    return sh(
            script: getShEchoResultCmd,
            returnStdout: true
    ).trim()
}

String genYidunConfigText() {
    // default "office"、"heart"、"jasmine"
    String[] config = ["${WORKSPACE}/app/peony.jks", 'peony', 'cikJayHiow', 'Pixiznuf']
    switch (P_BUILD_FLAVOR) {
        case "poinsettia":
            config[0] = "${WORKSPACE}/app/emerald.jks" as String
            config[1] = "emerald"
            config[2] = "cikJayHiow"
            config[3] = "cikJayHiow"
            break
        case "hyacinth":
            config[0] = "${WORKSPACE}/app/hyacinth.jks" as String
            config[1] = "hyacinth"
            config[2] = "cikJayHiow"
            config[3] = "cikJayHiow"
            break
    }
    return """
[appkey]
key=836e5705ed124e93a6ef30bdf16542c1e334

[apksign]
keystore=${config[0] as String}
alias=${config[1]}
pswd=${config[2]}
aliaspswd=${config[3]}
signmod=0

[Channel]
path=channel.txt
"""
}

// 所有渠道信息
static String getFullChannels() {
    return '''
OFFICIAL|UMENG_CHANNEL
YINGYONGBAO|UMENG_CHANNEL
YINGYONGBAO_WHITE|UMENG_CHANNEL
HUAWEI|UMENG_CHANNEL
XIAOMI|UMENG_CHANNEL
OPPO|UMENG_CHANNEL
VIVO|UMENG_CHANNEL
BAIDU|UMENG_CHANNEL
SAMSUNG|UMENG_CHANNEL
DOUYIN|UMENG_CHANNEL
DOUYIN_WHITE|UMENG_CHANNEL
KUAISHOU|UMENG_CHANNEL
KUAISHOU_WHITE|UMENG_CHANNEL
CPS|UMENG_CHANNEL
20001|UMENG_CHANNEL
20002|UMENG_CHANNEL
20003|UMENG_CHANNEL
20004|UMENG_CHANNEL
20005|UMENG_CHANNEL
20006|UMENG_CHANNEL
20007|UMENG_CHANNEL
20008|UMENG_CHANNEL
20009|UMENG_CHANNEL
20010|UMENG_CHANNEL
20011|UMENG_CHANNEL
20012|UMENG_CHANNEL
20013|UMENG_CHANNEL
20014|UMENG_CHANNEL
20015|UMENG_CHANNEL
20016|UMENG_CHANNEL
20017|UMENG_CHANNEL
20018|UMENG_CHANNEL
20019|UMENG_CHANNEL
20020|UMENG_CHANNEL
9001|UMENG_CHANNEL
9002|UMENG_CHANNEL
9003|UMENG_CHANNEL
9004|UMENG_CHANNEL
9005|UMENG_CHANNEL
600001|UMENG_CHANNEL
600002|UMENG_CHANNEL
600003|UMENG_CHANNEL
600004|UMENG_CHANNEL
600005|UMENG_CHANNEL
'''
}

static List<String> getFullChannelsOnly() {
    def newList = getFullChannels().collect { it.split("\\|")[0] }
    newList.sort { it }
    return newList
}

static def getDate() {
    return new Date().format('yyyyMMddHHmmss')
}

// 生成飞书卡片消息的标题及内容
String[] genLarkTitleAndMsg(buildResult, buildTime, buildTag) {
    // \ 需要转义，如 \n 要想换行符生效，需写成\\n
    def _title = ""
    def _msg = ""
    def _msg_color = ""
    StringBuilder sb = new StringBuilder()
    if (buildResult == "SUCCESS") {
        _title = "【" + env.JOB_NAME + "】#构建成功"
        _msg_color = "green"
    } else if (buildResult == "ABORTED") {
        _title = "【" + env.JOB_NAME + "】#构建被终止"
        _msg_color = "grey"
    } else {
        _title = "【" + env.JOB_NAME + "】#构建失败"
        _msg_color = "red"
    }
    sb.append("\\n构建名称：" + buildTag)
    sb.append("\\n构建分支：" + P_BUILD_BRANCH)
    sb.append("\\n构建时间：" + buildTime)
    sb.append("\\n查看详情：[项目地址](" + env.BUILD_URL + ")")
    def appPgyerURL = env.appShortcutUrl
    if (appPgyerURL != null && !appPgyerURL.isEmpty()) {
        sb.append(", [蒲公英下载地址](" + appPgyerURL + ")")
    }
    def appQRCodeURL = env.appQRCodeURL
    if (appQRCodeURL != null && !appQRCodeURL.isEmpty()) {
        sb.append(", [蒲公英二维码](" + appQRCodeURL + ")")
    }
    _msg = sb.toString()
    return [_title, _msg, _msg_color]
}

/**
 * 生成飞书消息
 * HTTP 请求的传入参数都是意json格式传入，所以不要出现单引号，全部用双引号
 * \ 需要转义，如 \n 要想换行符生效，需写成\\n
 */
static def genLarkMsg(msg_title, msg_text, color = "green") {
    def msg_data = """
{
  "msg_type":"interactive",
  "card":{
    "config":{
      "wide_screen_mode": true,
      "enable_forward": true
    },
    "elements":[
      {
        "tag": "div",
        "text": {
          "content": "${msg_text}",
          "tag": "lark_md"
        }
      }
    ],
    "header":{
      "template": "$color",
      "title":{
        "content": "${msg_title}",
        "tag": "plain_text"
      }
    }
  }
}
    """
    return msg_data
}