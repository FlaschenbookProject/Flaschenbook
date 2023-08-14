# Use the official Fluentd image
FROM fluent/fluentd:v1.12

# Use root account to use apt
USER root

# Install plugins as needed
RUN gem install fluent-plugin-elasticsearch \
    && gem install fluent-plugin-rewrite-tag-filter

# Copy configuration files
COPY ./fluentd.conf /fluentd/etc/

# Change to the fluent user
USER fluent
