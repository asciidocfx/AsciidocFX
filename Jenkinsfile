pipeline {
  agent any
  stages {
    stage('error') {
      steps {
        sh '/opt/apache-maven-3.3.9/bin/mvn clean install'
      }
    }
  }
}