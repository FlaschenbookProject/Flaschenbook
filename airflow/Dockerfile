# Use an official Airflow image as the base
ARG AIRFLOW_VERSION=2.6.2
FROM apache/airflow:${AIRFLOW_VERSION}

# The airflow user is already created in the base image
USER airflow

# Copy your dags and any other needed files
COPY ./dags /opt/airflow/dags
COPY ./scripts /opt/airflow/scripts

# Install additional dependencies from the requirements file
ADD requirements.txt .
RUN pip install apache-airflow==${AIRFLOW_VERSION} -r requirements.txt 

# Set environment variables
ENV AIRFLOW_HOME=/opt/airflow

# Expose ports
EXPOSE 8080

# Start airflow
CMD ["airflow", "scheduler"]
