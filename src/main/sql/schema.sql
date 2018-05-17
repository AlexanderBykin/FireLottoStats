
/*
  DROP TABLE IF EXISTS player_wallet;
  DROP TABLE IF EXISTS ticket;
  DROP TABLE IF EXISTS draw;
  DROP TABLE IF EXISTS draw_wallet;
  DROP TABLE IF EXISTS draw_base;
*/

CREATE TABLE IF NOT EXISTS draw_base
(
    id serial NOT NULL,
    name character varying(15) NOT NULL,
    numbers smallint NOT NULL,
    max_numbers SMALLINT NOT NULL,
    CONSTRAINT draw_base_pkey PRIMARY KEY (id)
);

INSERT INTO draw_base VALUES(1, '4x20', 4, 20);
INSERT INTO draw_base VALUES(2, '5x36', 5, 36);
INSERT INTO draw_base VALUES(3, '6x45', 6, 45);

CREATE TABLE IF NOT EXISTS draw_wallet
(
    id serial NOT NULL,
    draw_id integer NOT NULL,
    wallet character varying(42) NOT NULL,
    start_game_index SMALLINT NOT NULL,
    last_block_num bigint NOT NULL DEFAULT 0,
    CONSTRAINT draw_wallet_pkey PRIMARY KEY (id),
    CONSTRAINT draw_wallet_uniq UNIQUE (draw_id, wallet)
);

-- 4x20
INSERT INTO draw_wallet VALUES(1, 1, '0x2020fE9fA0f43fDe44360AAe03138C4B6AB35055', 77, 0);
INSERT INTO draw_wallet VALUES(2, 1, '0xa0306fCaE88f84CBBe2CF784B1046A94DeF54015', 25, 0);
INSERT INTO draw_wallet VALUES(3, 1, '0x9f3eae582f7541e673fe486900cc5539b8f24c8e', 0, 0);
-- 5x36
INSERT INTO draw_wallet VALUES(4, 2, '0x10C621008B210C3A5d0385e458B48af05BF4Ec88', 82, 0);
INSERT INTO draw_wallet VALUES(5, 2, '0xd06FD155421a993057113f5e2597Bf65b9f42b00', 25, 0);
INSERT INTO draw_wallet VALUES(6, 2, '0xc1abd580ac9545f771f3c430bbfdc8792ff3f987', 0, 0);
-- 6x45
INSERT INTO draw_wallet VALUES(7, 3, '0x2734B99e2C62cA70fE753a0cbcD3a90930E1EEC9', 81, 0);
INSERT INTO draw_wallet VALUES(8, 3, '0xE044e2eb641Efa48B97ef05c24f70F7675D2D404', 24, 0);
INSERT INTO draw_wallet VALUES(9, 3, '0xcce900b307443c2b600a21b82c8e0b8d9279bfd1', 0, 0);

CREATE TABLE IF NOT EXISTS draw
(
    id bigserial NOT NULL,
    draw_id integer NOT NULL,
    draw_num integer NOT NULL,
    ticket_count smallint NOT NULL,
    num1 smallint NOT NULL,
    num2 smallint NOT NULL,
    num3 smallint NOT NULL,
    num4 smallint NOT NULL,
    num5 smallint,
    num6 smallint,
    CONSTRAINT draw_pkey PRIMARY KEY (id),
    CONSTRAINT draw_uniq UNIQUE (draw_id, draw_num),
    CONSTRAINT draw_draw_base_fk FOREIGN KEY (draw_id)
        REFERENCES draw_base (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS ticket
(
    id bigserial NOT NULL,
    draw_id integer NOT NULL,
    draw_num integer NOT NULL,
    draw_wallet_id integer NOT NULL,
    wallet character varying(42) NOT NULL,
    ticket_number integer NOT NULL,
    purchase_date bigint NOT NULL,
    win_amount character varying(128) NOT NULL,
    ticket_price character varying(128) NOT NULL,
    num1 smallint NOT NULL,
    num2 smallint NOT NULL,
    num3 smallint NOT NULL,
    num4 smallint NOT NULL,
    num5 smallint,
    num6 smallint,
    CONSTRAINT ticket_pkey PRIMARY KEY (id),
    CONSTRAINT ticket_uniq UNIQUE (draw_id, draw_num, wallet, ticket_number),
    CONSTRAINT ticket_draw_base_fk FOREIGN KEY (draw_id)
        REFERENCES draw_base (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT ticket_draw_wallet_fk FOREIGN KEY (draw_wallet_id)
        REFERENCES draw_wallet (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

--- загружаются через etherscan и потом кладутся в Set чтобы найти только уникальные кошельки игроков
CREATE TABLE IF NOT EXISTS player_wallet
(
    id bigserial NOT NULL,
    draw_id integer NOT NULL,
    wallet character varying(42) NOT NULL,
    last_loaded_ticket integer NOT NULL DEFAULT 0,
    CONSTRAINT player_wallet_pkey PRIMARY KEY (id),
    CONSTRAINT player_wallet_uniq UNIQUE (draw_id, wallet),
    CONSTRAINT player_wallet_draw_base_fk FOREIGN KEY (draw_id)
        REFERENCES draw_base (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS player_wallet_has_ticket
(
    draw_id integer NOT NULL,
    draw_wallet_id integer NOT NULL,
    wallet character varying(42) NOT NULL
);