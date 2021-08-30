pipeline {
  agent any
  stages {
    stage('clean') {
      steps {
        sh 'mvn clean'
      }
    }

    stage('build') {
      steps {
        sh 'mvn package'
      }
    }

    stage('') {
      steps {
        archiveArtifacts(fingerprint: true, artifacts: './target/*.jar')
      }
    }

  }
  tools {
    maven 'maven'
    jdk 'JDK16'
  }
}