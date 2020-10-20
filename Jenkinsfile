#!groovy​

node {

  currentBuild.result = "SUCCESS"
  def port = 7969

  def myproject = 'dhs-audience-service'
  def recipients = "paddu.vedam+${myproject}@sharecare.com, jared.hooper+${myproject}@sharecare.com"

  env.JAVA_HOME="/usr/lib/jvm/java-1.8.0-amazon-corretto"
  env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
  try {

    // the abusive use of stages is to aid in debugging the individual build tasks
    stage('checkout') {
      git credentialsId: '398085af-5037-4815-b85a-e10c8f8faf5a', url: 'git@github.com:Sharecare/dhs-audience-service.git', branch: params.BRANCH
    }

    stage('sbt-clean') {
      def sbtHome = tool name: 'SBT', type: 'org.jvnet.hudson.plugins.SbtPluginBuilder$SbtInstallation'
      sh "echo \$(java -version)"
      sh "'${sbtHome}/bin/sbt' -Dsbt.log.format=false sbtVersion clean"
    }

    stage('sbt-test') {
      def sbtHome = tool name: 'SBT', type: 'org.jvnet.hudson.plugins.SbtPluginBuilder$SbtInstallation'
      sh "DB_PORT=58017 '${sbtHome}/bin/sbt' -Dsbt.log.format=false test"
    }

    stage('sbt-package') {
      def sbtHome = tool name: 'SBT', type: 'org.jvnet.hudson.plugins.SbtPluginBuilder$SbtInstallation'
      sh "'${sbtHome}/bin/sbt' -Dsbt.log.format=false universal:packageZipTarball"
    }

//    stage('sbt-newman') {
//      def sbtHome = tool name: 'SBT', type: 'org.jvnet.hudson.plugins.SbtPluginBuilder$SbtInstallation'
//      //sh "'${sbtHome}/bin/sbt' -Dsbt.log.format=false -Dnewman.APPLICATION_PORT=${port} -Dnewman.DB_PORT=58017 newman:immutable_deprecated"
//      //sh "'${sbtHome}/bin/sbt' -Dsbt.log.format=false -Dnewman.APPLICATION_PORT=${port} -Dnewman.DB_PORT=58017 -Dnewman.COLLECTION_JSON=newman/vitals.json newman:mutable_deprecated"
//      //sh "'${sbtHome}/bin/sbt' -Dsbt.log.format=false -Dnewman.APPLICATION_PORT=${port} -Dnewman.DB_PORT=58017 -Dnewman.COLLECTION_JSON=newman/vitals-biometrics.json newman:mutable_deprecated"
//    }

//    stage('loadtest') {
//      //sh "./loadtest.sh ${port} localhost 58017"
//    }

    stage('upload') {
      def mypackage = "dhs-audience-service"
      sh "/opt/scripts/jenkins-s3Put.py -b admin-sharecare -f target/universal/${mypackage}.tgz -o builds/${mypackage}/dev/${BUILD_NUMBER}/${mypackage}.tgz"
      sh "/opt/scripts/jenkins-s3Put.py -b admin-sharecare -f target/universal/${mypackage}.tgz -o builds/${mypackage}/rc/${BUILD_NUMBER}/${mypackage}.tgz"
      sh "/opt/scripts/jenkins-s3Put.py -b admin-scfort -f target/universal/${mypackage}.tgz -o builds/${mypackage}/rc/${BUILD_NUMBER}/${mypackage}.tgz"
//      sh "/opt/scripts/jenkins-s3Put.py -b admin-br -f target/universal/${mypackage}.tgz -o builds/${mypackage}/rc/${BUILD_NUMBER}/${mypackage}.tgz"
    }

    emailext (subject: "Job '${env.JOB_NAME}' status: ${currentBuild.result}",
      to: recipients,
      mimeType: 'text/html',
      body: """
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8">
    <title>${env.JOB_NAME}</title>
    <style>
      body table, td, th, p, h1, h2 {
        margin:0;
        font:normal normal 100% Georgia, Serif;
        background-color: #ffffff;
      }
      h1, h2 {
        border-bottom:dotted 1px #999999;
        padding:5px;
        margin-top:10px;
        margin-bottom:10px;
        color: #000000;
        font: normal bold 130% Georgia,Serif;
        background-color:#f0f0f0;
      }
      tr.gray { background-color:#f0f0f0; }
      h2 {
        padding:5px;
        margin-top:5px;
        margin-bottom:5px;
        font: italic bold 110% Georgia,Serif;
      }
      .bg2 {
        color:black;
        background-color:#E0E0E0;
        font-size:110%
      }
      th { font-weight: bold; }
      tr, td, th { padding:2px; }
      td.test_passed { color:blue; }
      td.test_failed { color:red; }
      td.test_skipped { color:grey; }
      .console {
        font: normal normal 90% Courier New, monotype;
        padding:0px;
        margin:0px;
      }
      div.content, div.header {
        background: #ffffff;
        border: dotted
        1px #666;
        margin: 2px;
        content: 2px;
        padding: 2px;
      }
      table.border, th.border, td.border {
        border: 1px solid black;
        border-collapse:collapse;
      }
      td.right { text-align:right; }
    </style>
  </head>
  <body>
    <div class="header">
      <table>
        <tr class="gray">
          <td align="right"><img src="https://jenkins.alias.sharecare.com/jenkins/static/dd44e453/images/32x32/blue.png"></td>
          <td valign="center"><b style="font-size: 200%;">BUILD ${currentBuild.result}</b></td>
        </tr>
        <tr>
          <td>Build URL</td>
          <td><a ref="${env.BUILD_URL}">${env.BUILD_URL}</a></td>
        </tr>
        <tr>
          <td>Project:</td>
          <td>${env.JOB_NAME}</td>
        </tr>
        <tr>
          <td>Build Branch:</td>
          <td>${env.BRANCH_NAME}</td>
        </tr>
        <tr>
          <td>Date of build:</td>
          <td>${currentBuild.timeInMillis}</td>
        </tr>
        <tr>
          <td>Build duration:</td>
          <td>${currentBuild.duration}</td>
        </tr>
        <tr>
          <td>Build cause:</td>
          <td>Started by user ${env.CHANGE_AUTHOR}</td>
        </tr>
        <tr>
          <td>Build description:</td>
          <td>${currentBuild.description}</td>
        </tr>
        <tr>
          <td>Built on:</td>
          <td>${env.NODE_NAME}</td>
        </tr>
      </table>
    </div>
    <br>
  </body>
</html>""")

  } catch (err) {

    currentBuild.result = "FAILURE"

    emailext (subject: "Job '${env.JOB_NAME}' (${env.BUILD_NUMBER}) status: ${currentBuild.result}",
      to: recipients,
      mimeType: 'text/html',
      attachLog: true,
      compressLog: true,
      body: """
<html>
  <body>
  <p>Encountered a problem when attempting to build <a href="${env.JOB_URL}">${env.JOB_NAME}</a>.</p>
  </br>
  <p>Build logs are attached. Full details accessible <a href="${env.BUILD_URL}">${env.BUILD_URL}</a>.</p>
  </br>
  </body>
</html>""")

    throw err

  }
}

