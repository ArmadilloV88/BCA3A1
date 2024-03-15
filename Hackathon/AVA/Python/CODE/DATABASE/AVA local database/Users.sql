CREATE TABLE Users (
    [Id]            INT             IDENTITY (1, 1) NOT NULL,
    [Username]      NVARCHAR (MAX)  NULL,
    [PasswordHash]  VARBINARY (MAX) NULL,
    [Salt]          VARBINARY (MAX) NULL,
    [Hash]          VARBINARY (MAX) NULL,
    [CombinedBytes] NVARCHAR (4000) NULL,
    CONSTRAINT [PK_Users] PRIMARY KEY CLUSTERED ([Id] ASC)
);