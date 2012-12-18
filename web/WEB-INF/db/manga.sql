begin;

create table manga ( 
       id integer primary key,
       title varchar(255) unique, 
       author varchar(255),
       artist varchar(255),
       publisher varchar(255),
       circle varchar(255),
       scan_grp varchar(255),
       description varchar(4096),
       published_on date,
       uploaded_at timestamp default CURRENT_TIMESTAMP,
       updated_at timestamp default CURRENT_TIMESTAMP,
       complete boolean default false,
       mature boolean default true,
       req_role varchar(32) default 'none'
);

create table chapter (
       chap_id integer primary key,
       manga_id int not null,
       chap_title varchar(255),
       uploaded_at timestamp default CURRENT_TIMESTAMP,
       foreign key (manga_id) references manga(id),
       unique(manga_id, chap_title)
);

create table tag_cv (
       tag varchar(32) primary key
);

create table tag (
       tag varchar(32),
       manga_id int,
       primary key (manga_id, tag),
       foreign key (manga_id) references manga(id),
       foreign key (tag) references tag_cv(tag)
);

create table manga_user (
       username varchar(32) primary key,
       display_name varchar(32) not null,
       joined_at timestamp default CURRENT_TIMESTAMP,
       userpass varchar(64)
);

create table user_role (
       username varchar(32),
       userrole varchar(32),
       primary key (username, userrole),
       foreign key (username) references manga_user(username)
);

commit;
