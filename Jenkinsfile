pipeline {
  agent any

  stages {
    stage('build') {
      steps {
        sh './gradlew build'
      }
    }

    stage('sonarqube') {
      environment {
        SONAR_AUTH_TOKEN = credentials('sonarqube-cred')
      }
      steps {
        withSonarQubeEnv(installationName: 'sonarqube-server', credentialsId: 'sonarqube-cred') {
          sh "./gradlew sonar \
                -Dsonar.projectKey=${projectKey} \
                -Dsonar.host.url=${sonarUrl} \
                -Dsonar.login=${SONAR_AUTH_TOKEN}"
        }

      }
    }

    stage("Quality Gate") {
        steps {
            timeout(time: 1, unit: 'HOURS') {
                waitForQualityGate abortPipeline: true
            }
        }
    }

    stage('deploy') {
      steps {
        build 'stage4-task1-deploy'
      }
    }

  }
}