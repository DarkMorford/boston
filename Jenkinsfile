pipeline {
    agent {
        docker {
            image 'openjdk:8'
            args  '--volume mc-gradle:/root/.gradle'
        }
    }

    options {
        timestamps
    }

    stages {
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
    }
}
