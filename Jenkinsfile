node {
    def repoName = "service-batch-converter"
    def artifactName = "${repoName}-0.1.${BUILD_NUMBER}.jar"
    def artifactPomName = "${repoName}-0.1.${BUILD_NUMBER}.pom"

    stage('scm') {
        dir(repoName) {
            git branch: 'master',
            credentialsId: 'mycredentials',
            url: 'http://feronti@bitbucket.viridian.cc/scm/stat/' + repoName + '.git'
        }
        sh('du -hcs *')
    }
    stage('Build') {
       echo "building " + artifactName
        dir(repoName) {
            sh 'sed -i "s/.9999/.${BUILD_NUMBER}/g" pom.xml'
            sh 'sed -i "s/\\\${artifact.version}/0.1.${BUILD_NUMBER}/g" pom.xml'
            sh "mvn  -DskipTests clean compile"
        }
    }
    stage('Checkstyle') {
        dir(repoName) {
            sh "mvn checkstyle:checkstyle -Dcheckstyle.config.location=viridian_checks.xml"
            publishHTML ( [
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: false,
                reportDir: 'target/site',
                reportFiles: 'checkstyle.html',
                reportName: 'HTML Report',
                reportTitles: ''
            ])

            step([
                $class: 'CheckStylePublisher',
                pattern: '**/checkstyle-result.xml',
                unstableTotalHigh: '183',
                unstableTotalNormal: '80',
                unstableNewHigh: '0',
                unstableNewNormal: '0',

                usePreviousBuildAsReference: true
            ])
        }
    }
    stage('test') {
        dir(repoName) {
            sh "mvn  -Dmaven.test.failure.ignore package"
        }
        //junit '**/target/surefire-reports/TEST-*.xml'
    }
    stage("deploy") {

        dir(repoName) {
            sh "mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file -Dfile=target/" + artifactName +" -DgeneratePom=true -Dpackaging=jar"

            def curlCmd = "curl -u jenkins:sesamo -X PUT \"http://desarrollo.viridian.cc:8081/artifactory/libs-release-local/cc/viridian" \
                + "/" + repoName + "/0.1.${BUILD_NUMBER}/" + artifactName + "\" -T " \
                + "~/.m2/repository/cc/viridian/" + repoName + "/0.1.${BUILD_NUMBER}/" + artifactName

            echo curlCmd

            sh curlCmd

            def curlCmdPom = "curl -u jenkins:sesamo -X PUT \"http://desarrollo.viridian.cc:8081/artifactory/libs-release-local/cc/viridian" \
                + "/" + repoName + "/0.1.${BUILD_NUMBER}/" + artifactPomName + "\" -T " \
                + "~/.m2/repository/cc/viridian/" + repoName + "/0.1.${BUILD_NUMBER}/" + artifactPomName

            echo curlCmdPom

            sh curlCmdPom

            def committerEmail = sh (
                 script: 'git log -1 --no-merges --pretty=format:\'%an\' ',
                 returnStdout: true
             ).trim()

            def summary = sh (
                 script: 'git log -1 --no-merges --pretty=format:\'%s\' ',
                 returnStdout: true
            ).trim()

            def slackColor = 'good';
            if (currentBuild.result == "UNSTABLE") {
                slackColor = 'danger';
            }
            def slackFooter = '';
            if (currentBuild.result != "SUCCESS") {
                slackFooter = "\n`${currentBuild.result}`";
            }
            slackSend color: slackColor,
                message: "*" + artifactName + "*\n" + summary + "\n_" + committerEmail + "_" + slackFooter


            sh '/var/lib/jenkins/viridian/deploy-' + repoName + '.sh'
        }

        sh '/var/lib/jenkins/viridian/deploy-' + repoName + '.sh'
    }
}