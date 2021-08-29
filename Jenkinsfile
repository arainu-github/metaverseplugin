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

  }
  tools {maven 'maven'}
}
