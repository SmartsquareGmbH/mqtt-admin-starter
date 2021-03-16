FROM emqx/emqx:4.2.7
COPY emqx_auth_mnesia.conf /opt/emqx/etc/plugins/emqx_auth_mnesia.conf
ENV EMQX_LOADED_PLUGINS emqx_management,emqx_dashboard,emqx_auth_clientid,emqx_auth_mnesia
