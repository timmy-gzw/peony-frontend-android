pipeline {
    agent {
        label 'android'
    }
    stages {
        stage('Compile') {
            steps {
                script {
                    sh 'echo "Now Compile the source code"'
                    sh 'env | grep $HOME'
                    sh 'chmod +x ./gradlew'
                    sh './gradlew assembleOfficeDebug -i'
                }
            }
        }
    }
}