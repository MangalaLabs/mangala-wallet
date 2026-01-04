package com.mangala.wallet.features.nft_base.data.repository.mapper

import com.mangala.wallet.features.nft_base.domain.model.NftCollection
import com.mangala.wallet.features.nft_base.domain.model.NftType
import com.mangala.wallet.model.provider.covalenthq.GetNftsForAddressResponse
import com.mangala.wallet.utils.ext.toLong
import commangalawalletdatabase.NftCollectionEntity
import commangalawalletdatabase.NftEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun GetNftsForAddressResponse.toNfts(): List<NftCollection> {
    return data?.items?.map {
        NftCollection(
            contractName = it?.contractName.orEmpty(),
            contractTickerSymbol = it?.contractTickerSymbol.orEmpty(),
            contractAddress = it?.contractAddress.orEmpty(),
            nft = it?.nftData.toNftDataList(),
            type = if (it?.supportsErc?.contains("erc1155") == true) NftType.ERC1155 else NftType.ERC721
        )
    } ?: emptyList()
}

fun List<NftCollection.Nft>.toNftEntityList(
    blockchainUid: String,
    accountId: String,
    collectionContractAddress: String
) = map {
    NftEntity(
        tokenId = it.tokenId,
        tokenUrl = it.tokenUrl,
        name = it.name,
        description = it.description,
        image = it.image,
        blockchain_uid = blockchainUid,
        accountId = accountId,
        attributes = Json.encodeToString(it.attributes),
        collectionContractAddress = collectionContractAddress,
        isFavorite = it.isFavorite.toLong()
    )
}

private fun List<GetNftsForAddressResponse.Data.Item.NftData?>?.toNftDataList() =
    this?.map { nftData ->
        NftCollection.Nft(
            tokenId = nftData?.tokenId.orEmpty(),
            tokenUrl = nftData?.tokenUrl.orEmpty(),
            name = nftData?.externalData?.name.orEmpty(),
            description = nftData?.externalData?.description.orEmpty(),
            image = nftData?.externalData?.image.orEmpty(),
            attributes = nftData?.externalData?.attributes?.map { attribute ->
                NftCollection.Nft.Attribute(
                    traitType = attribute.traitType.orEmpty(),
                    value = attribute.value.orEmpty()
                )
            } ?: emptyList(),
            isFavorite = false
        )
    } ?: emptyList()

fun GetNftsForAddressResponse.toNftCollectionEntity(blockchainUid: String, accountId: String): List<NftCollectionEntity> {
    return data?.items?.map {
        NftCollectionEntity(
            contractName = it?.contractName.orEmpty(),
            contractTickerSymbol = it?.contractTickerSymbol.orEmpty(),
            contractAddress = it?.contractAddress.orEmpty(),
            blockchain_uid = blockchainUid,
            accountId = accountId,
            type = if (it?.supportsErc?.contains("erc1155") == true) NftType.ERC1155.name else NftType.ERC721.name
        )
    } ?: emptyList()
}

fun List<GetNftsForAddressResponse.Data.Item.NftData?>?.mapToNftEntityList(
    json: Json,
    blockchainUid: String,
    accountId: String,
    collectionContractAddress: String
): List<NftEntity> {
    return this?.map { nftData ->
        val attributes = nftData?.externalData?.attributes?.map {
            NftCollection.Nft.Attribute(
                traitType = it.traitType.toString(),
                value = it.value.toString()
            )
        }

        NftEntity(
            tokenId = nftData?.tokenId.orEmpty(),
            tokenUrl = nftData?.tokenUrl.orEmpty(),
            name = nftData?.externalData?.name.orEmpty(),
            description = nftData?.externalData?.description.orEmpty(),
            image = nftData?.externalData?.image.orEmpty().replace(
                "https://ipfs.io/ipfs/ipfs",
                "https://ipfs.io/ipfs"
            ), // Fixes issue with Covalent data source having invalid URL
            blockchain_uid = blockchainUid,
            accountId = accountId,
            attributes = json.encodeToString(attributes?.map { attribute ->
                NftCollection.Nft.Attribute(
                    traitType = attribute.traitType,
                    value = attribute.value
                )
            } ?: emptyList()),
            collectionContractAddress = collectionContractAddress,
            isFavorite = false.toLong()
        )
    } ?: emptyList()
}

fun List<NftEntity>.toNfts() = this.map { it.toNft() }

fun List<NftCollection>.toNftCollectionEntityList(accountId: String, blockchainUid: String): List<NftCollectionEntity> {
    return map {
        it.toNftCollectionEntity(accountId, blockchainUid)
    }
}

fun NftEntity.toNft() = NftCollection.Nft(
    tokenId = tokenId,
    tokenUrl = tokenUrl,
    name = name,
    description = description,
    image = image,
    attributes = Json.decodeFromString(attributes),
    isFavorite = isFavorite == 1L
)

fun NftCollection.toNftCollectionEntity(accountId: String, blockchainUid: String): NftCollectionEntity {
    return NftCollectionEntity(
        contractName = contractName,
        contractTickerSymbol = contractTickerSymbol,
        contractAddress = contractAddress,
        type = type.name,
        accountId = accountId,
        blockchain_uid = blockchainUid
    )
}

fun NftCollectionEntity.toNft(nfts: List<NftCollection.Nft>): NftCollection {
    return NftCollection(
        contractName = contractName,
        contractTickerSymbol = contractTickerSymbol,
        contractAddress = contractAddress,
        type = NftType.valueOf(type),
        nft = nfts
    )
}